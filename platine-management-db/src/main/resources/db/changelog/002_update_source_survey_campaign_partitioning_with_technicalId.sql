<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

    <!-- Changeset to add technicalId column -->
    <changeSet id="002" author="ethuaud">
        <!-- Add the new technicalId column as UUID -->
        <addColumn tableName="campaign">
            <column name="technicalId" type="uuid">
                <constraints nullable="false"/>
            </column>
        </addColumn>

        <!-- Update existing rows to set random UUID for the technicalId column -->
        <update tableName="campaign">
            <column name="technicalId" valueComputed="gen_random_uuid()"/>
        </update>

        <!-- Add unique constraint on the technicalId column -->
        <addUniqueConstraint columnNames="technicalId" tableName="campaign"/>
    </changeSet>

</databaseChangeLog>
