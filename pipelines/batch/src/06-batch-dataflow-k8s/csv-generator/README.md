# CSV Generator

## Data

### Class

```java
public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String title;
    private String email;
    private String phone;
    private Date birth;
    private String address;
    private String street;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String company;
    private String creditCardNumber;
    private String jobTitle;
    private Date startDate;
    private Date endDate;
}
```


### Order (columns)

```
    id,
    name.firstName(),
    name.lastName(),
    name.fullName(),
    title,
    email,
    phone,
    birth.toString(),
    address.fullAddress(),
    address.streetName(),
    address.cityName(),
    address.zipCode(),
    address.country(),
    address.state(),
    company,
    creditCardNumber,
    jobTitle,
    startDate.toString(),
    endDate.toString()
```

### Columns CSV file

```
"id","firstName","lastName","fullName","title","email","phone","birth","address","street","city","zipCode","country","state","company","creditCardNumber","jobTitle","startDate","endDate"
3dfaf2b6-309c-4b93-8f06-b1aa90c0d95d,Paulene,Moen,Danuta Christiansen,National Markets Developer,basil.conn@gmail.com,(286) 031-6875,Wed Aug 30 09:37:26 CEST 2017,"091 Howard Manors, Lake Jaime, OH 22009-7111",Heidenreich Ports,Harveyton,23551-0061,Botswana,Indiana,Braun Inc,934-49-0088,Dynamic Real-Estate Administrator,Tue Jul 25 21:01:37 CEST 2017,Fri Apr 05 22:18:57 CEST 2019
7ee69375-0aa1-4f1f-a3ff-e57088720be4,Francis,Carroll,Mr. Sanford Beahan,Global Tactics Facilitator,wendie.waters@hotmail.com,1-950-646-1619,Fri Jul 27 17:10:52 CEST 1984,"Apt. 969 71446 Marcellus Ports, West Shemeka, MA 35073",Perla Circle,Lucasberg,48919-8259,Burundi,New Jersey,Metz and Sons,972-74-2771,Corporate Director,Tue Jun 05 10:56:29 CEST 2018,Sat Apr 06 17:38:38 CEST 2019
7c1e879e-3ed4-493c-8535-c284ecfc558e,Lessie,Wilderman,Ms. Shaun Zemlak,Product Response Planner,gema.moen@gmail.com,1-449-659-8921,Mon Dec 10 23:00:47 CET 1979,"291 Medhurst Ports, South Jeffry, SC 09766",Arlette Camp,Littlechester,90064,Georgia,Kentucky,Kassulke Group,000-97-5312,Senior Construction Director,Thu Nov 10 20:22:24 CET 2016,Sat Apr 06 09:58:09 CEST 2019

```



### Example

## Values

- RowsPerPartition = 100000 rows (size?) // Maximum rows per partition (process), limiting the memory and overhead
- RowsPerCommit = 100 // Maximun rows per commit in bulk operation. Bigger values, worst results if the process fails.
- ChunksPerPartition = 100000 / chunks? = RowsPerCommit (chunk)  // This is the number of chunks or iterations per partition (max)

## Computation (split process)

RowsTotal = 10000000 (3.25GB)
Partitions = 10000000 / RowsPerPartition (100000) = 100 partitions

## Split process

The split process will consist, first in generate the file and them split the file into chunks.

The file is split by lines, since it is the minimal logic unit, and it cannot be split into different files to be processed.

```bash

docker run -it -v /tmp/test:/tmp/test busybox /bin/sh

cd /tmp/test

split -l 100000 -d -a 2 sample-data.csv sample-data-

# Finally rename files to .csv
rename "s/oldExtension/newExtension/" *.csv

```

| Time | Start | End | Processes |
| -- | -- | -- | -- |
| | 20:32 | 20:45 | (1.2GB)  |
| | 20:32 | 21:07 | (3.25GB) |

Parallel split??

The process must generate the `md5sum` in order to verify the integrity of the files split.
https://askubuntu.com/questions/318530/generate-md5-checksum-for-all-files-in-a-directory

## References

- [A Guide to JavaFaker](https://www.baeldung.com/java-faker)
- [Introduction to OpenCSV](https://www.baeldung.com/opencsv)
- [Spring Boot Console Application](https://www.baeldung.com/spring-boot-console-app)