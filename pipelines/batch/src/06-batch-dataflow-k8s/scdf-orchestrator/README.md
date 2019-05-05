# README


## Liquibase

- Check the current status `main`

        gradle status
   
- Check the current differences between the defined model entities and database
    
        gradle diff -PrunList='diffLog'
    
- Update Current liquibase migration definitions with current state.

        gradle diffChangeLog -PrunList='diffLog'
        
- The output must create a new changelog file `20190505105822_changelog.xml` with the delta

    > NOTE: Since there is more tables inside the default schema, liquibase tries to remove all of them.
    
    ```xml
    <?xml version="1.1" encoding="UTF-8" standalone="no"?>
    <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">
        <changeSet author="jsantosa (generated)" id="1557046706950-1">
            <createTable tableName="custom_task_schedule">
                <column autoIncrement="true" name="id" type="INTEGER">
                    <constraints primaryKey="true" primaryKeyName="custom_task_schedulePK"/>
                </column>
                <column name="created_time" type="BYTEA">
                    <constraints nullable="false"/>
                </column>
                <column name="data" type="VARCHAR(4096)"/>
                <column name="name" type="VARCHAR(512)">
                    <constraints nullable="false"/>
                </column>
                <column name="status" type="VARCHAR(256)">
                    <constraints nullable="false"/>
                </column>
                <column name="updated_time" type="BYTEA">
                    <constraints nullable="false"/>
                </column>
            </createTable>
        </changeSet>
    </databaseChangeLog>
    ``` 

   
- Finally, add current filename generated into `config/liquibase/master.xml`

    ``` xml
    <?xml version="1.0" encoding="utf-8"?>
    <databaseChangeLog
            xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.5.xsd">
    
        <include file="config/liquibase/changelog/00000000000000_initial_schema.xml" relativeToChangelogFile="false"/>
        <include file="config/liquibase/changelog/20190505105822_changelog.xml" relativeToChangelogFile="false"/>
    </databaseChangeLog>
    ``` 
    
- The database updaet can be done using:

  - Start (`BootRun`) directly the micro-service, so it bootstrap the current status (versioning)
  - Run following command:  `gradle update`