package fr.insee.survey.datacollectionmanagement.user.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service pour l'importation et la validation des affectations de portefeuilles (Wallets)
 * à partir de fichiers CSV ou JSON.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

  // Dépendances injectées par Spring (via @RequiredArgsConstructor)
  private final UserService userService;
  private final SurveyUnitService surveyUnitService;
  private final ObjectMapper objectMapper;

  // Constantes pour les en-têtes (utilisées pour CSV et JSON)
  private static final String HEADER_SURVEY_UNIT = "surveyUnit";
  private static final String HEADER_INTERNAL_USER = "internal_user";
  private static final String HEADER_GROUP = "group";
  private static final String[] REQUIRED_HEADERS = {
      HEADER_SURVEY_UNIT,
      HEADER_INTERNAL_USER,
      HEADER_GROUP
  };

  // Autorisé : uniquement lettres et chiffres (pas d'espaces, pas de caractères spéciaux)
  private static final Pattern VALID_TEXT = Pattern.compile("^[A-Za-z0-9]+$");

  @Override
  public void importWallets(String sourceId, MultipartFile file) {
    String filename = file.getOriginalFilename();
    if (filename == null || filename.isBlank()) {
      throw new IllegalArgumentException("Filename is missing");
    }

    log.info("Start import wallets, sourceId={}, file={}", sourceId, filename);

    try {
      Map<String, String> surveyUnitToInternalUser = new HashMap<>(); // Contrainte « UE -> un seul gestionnaire »
      Map<String, String> internalUserToGroup = new HashMap<>();      // Contrainte « gestionnaire -> un seul groupe »

      if (filename.endsWith(".csv")) {
        processCsv(file, surveyUnitToInternalUser, internalUserToGroup);
      } else if (filename.endsWith(".json")) {
        processJson(file, surveyUnitToInternalUser, internalUserToGroup);
      } else {
        throw new IllegalArgumentException("Unsupported file type. Only CSV or JSON are allowed.");
      }

      // Validation de la cohérence BDD (performante) après le parsing complet du fichier
      validateDatabaseConsistency(surveyUnitToInternalUser, internalUserToGroup);

      log.info("Wallets import validated successfully for sourceId={}", sourceId);

    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + e.getMessage(), e);
    }
  }

  /* =============================
     ==     FILE PARSING       ==
     ============================= */

  private void processCsv(
      MultipartFile file,
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup
  ) throws IOException {

    CSVFormat format = CSVFormat.Builder.create()
        .setDelimiter(',')
        .setHeader(REQUIRED_HEADERS)
        .setSkipHeaderRecord(true)
        .setTrim(true)
        .build();

    try (
        InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
        CSVParser csvParser = format.parse(reader)
    ) {

      List<String> headerNames = csvParser.getHeaderNames();
      if (headerNames == null || headerNames.isEmpty()) {
        throw new IllegalArgumentException("CSV header is missing or empty.");
      }
      Set<String> headerNameSet = new HashSet<>(headerNames);
      if (!headerNameSet.containsAll(List.of(REQUIRED_HEADERS))) {
        throw new IllegalArgumentException("CSV header must contain: "
            + HEADER_SURVEY_UNIT + ", "
            + HEADER_INTERNAL_USER + ", "
            + HEADER_GROUP);
      }

      int lineNo = 1;
      for (CSVRecord csvRecord : csvParser) {
        lineNo++;

        String surveyUnit = trimOrEmpty(csvRecord.get(HEADER_SURVEY_UNIT));
        String internalUser = trimOrEmpty(csvRecord.get(HEADER_INTERNAL_USER));
        String group = trimOrEmpty(csvRecord.get(HEADER_GROUP));

        validateFields(surveyUnit, internalUser, group, lineNo);
        validateInFileBusinessRules(surveyUnit, internalUser, group, surveyToUser, userToGroup, "line " + lineNo);
      }

      if (lineNo == 1) {
        throw new IllegalArgumentException("CSV is empty");
      }
    }
  }

  private void processJson(
      MultipartFile file,
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup
  ) throws IOException {

    List<Map<String, String>> rows = objectMapper.readValue(
        file.getInputStream(),
        new TypeReference<>() {}
    );

    if (rows == null || rows.isEmpty()) {
      throw new IllegalArgumentException("JSON array is empty");
    }

    int idx = 0;
    for (Map<String, String> row : rows) {
      idx++;
      String surveyUnit = trimOrEmpty(row.get(HEADER_SURVEY_UNIT));
      String internalUser = trimOrEmpty(row.get(HEADER_INTERNAL_USER));
      String group = trimOrEmpty(row.get(HEADER_GROUP));

      validateFields(surveyUnit, internalUser, group, idx);
      validateInFileBusinessRules(surveyUnit, internalUser, group, surveyToUser, userToGroup, "item " + idx);
    }
  }

  /* =============================
     ==       VALIDATIONS       ==
     ============================= */

  /**
   * Valide la syntaxe et le format des champs (non vide, pas de caractères spéciaux).
   */
  private void validateFields(String surveyUnit, String internalUser, String group, int position) {
    if (surveyUnit.isBlank() || internalUser.isBlank() || group.isBlank()) {
      throw new IllegalArgumentException("Record " + position + ": all columns must be non-null and non-empty");
    }
    if (!VALID_TEXT.matcher(surveyUnit).matches()
        || !VALID_TEXT.matcher(internalUser).matches()
        || !VALID_TEXT.matcher(group).matches()) {
      throw new IllegalArgumentException("Record " + position + ": fields contain forbidden special characters");
    }
  }

  /**
   * Valide les règles métier de cohérence interne au fichier (Règles 3 et 4).
   * Ces vérifications n'appellent pas la base de données.
   */
  private void validateInFileBusinessRules(
      String surveyUnit,
      String internalUser,
      String group,
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup,
      String where
  ) {
    // 3) UE -> un seul gestionnaire
    String alreadyUser = surveyToUser.putIfAbsent(surveyUnit, internalUser);
    if (alreadyUser != null && !alreadyUser.equals(internalUser)) {
      throw new IllegalArgumentException(
          "surveyUnit '" + surveyUnit + "' already assigned to '" + alreadyUser + "' (conflict at " + where + ")"
      );
    }

    // 4) gestionnaire -> un seul groupe
    String alreadyGroup = userToGroup.putIfAbsent(internalUser, group);
    if (alreadyGroup != null && !alreadyGroup.equalsIgnoreCase(group)) {
      throw new IllegalArgumentException(
          "internalUser '" + internalUser + "' already bound to group '" + alreadyGroup +
              "' (conflict with '" + group + "' at " + where + ")"
      );
    }
  }


  /**
   * Valide l'existence des entités en base de données APRES le parsing complet (Règles 1 et 2).
   * Optimisé pour ne vérifier que les identifiants uniques et pour rapporter tous les identifiants manquants.
   * * NOTE TECHNIQUE: Pour garantir les performances et collecter tous les manquants,
   * cette méthode suppose que les services ont été mis à jour pour offrir des méthodes
   * de recherche en masse (bulk find) qui renvoient l'ensemble des identifiants MANQUANTS.
   */
  private void validateDatabaseConsistency(
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup) {

    log.info("Validating database existence for {} unique survey units and {} unique users...",
        surveyToUser.size(), userToGroup.size());

    Set<String> uniqueUsers = userToGroup.keySet();
    Set<String> uniqueSurveyUnits = surveyToUser.keySet();

    // --- 1) VÉRIFICATION DES GESTIONNAIRES UNIQUES (User) ---
    Set<String> missingUsers = userService.findMissingIdentifiers(uniqueUsers);

    // --- 2) VÉRIFICATION DES UE UNIQUES (SurveyUnit) ---
    Set<String> missingSurveyUnits = surveyUnitService.findMissingIds(uniqueSurveyUnits);


    // --- 3) AGRÉGATION ET LEVÉE D'ERREUR ---
    if (!missingUsers.isEmpty() || !missingSurveyUnits.isEmpty()) {
      StringBuilder message = new StringBuilder("Database consistency validation failed:\n");

      if (!missingUsers.isEmpty()) {
        message.append("Missing Internal Users: ").append(String.join(", ", missingUsers)).append("\n");
      }
      if (!missingSurveyUnits.isEmpty()) {
        message.append("Missing Survey Units: ").append(String.join(", ", missingSurveyUnits)).append("\n");
      }
      throw new IllegalArgumentException(message.toString());
    }

    log.info("Database consistency validated.");
  }


  /* =============================
     ==          UTILS          ==
     ============================= */

  /**
   * Renvoie la chaîne "trimmée", ou une chaîne vide si l'entrée est nulle.
   * C'est essentiel pour prévenir les NullPointerExceptions dans validateFields.
   */
  private static String trimOrEmpty(String s) {
    return s == null ? "" : s.trim();
  }
}
