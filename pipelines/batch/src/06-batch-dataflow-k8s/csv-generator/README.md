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

time split -l 100000 -a 2 sample-data.csv sample-data-

for file in *; do mv "$file" "${file%}.csv"; done

```

| Time | Start | End | Processes |
| -- | -- | -- | -- |
| | 20:32 | 20:45 | (1.2GB)  |
| | 20:32 | 21:07 | (3.25GB) |

Parallel split??

The process must generate the `md5sum` in order to verify the integrity of the files split.
https://askubuntu.com/questions/318530/generate-md5-checksum-for-all-files-in-a-directory

## Compress process

Some compression techniques are going to be tested.
The file contains 1000000 records ~360MB

```bash

# Use alpine image container instead
docker run -it -v /tmp/test:/tmp/test alpine /bin/sh

cd /tmp/test

apk add p7zip

```

### GZip

- Bad compression

        time gzip -1 -k sample-data.csv

> 5.92s user 0.12s system 99% cpu 6.058 total ~186MB

- Mid compression

        time gzip -4 -k sample-data.csv

> 10.70s user 0.18s system 98% cpu 11.004 total ~170MB

- Best compression

        time gzip -9 -k sample-data.csv

> 29.69s user 0.54s system 99% cpu 30.515 total ~165MB

### Zip

- Bad compression

        time zip -1 sample-data.csv.zip sample-data.csv

> 7.58s user 0.35s system 98% cpu 8.020 total ~186MB

- Mid compression

        time zip -4 sample-data.csv.zip sample-data.csv

> 12.44s user 0.35s system 99% cpu 12.863 total ~169MB

- Best compression

        time zip -9 sample-data.csv.zip sample-data.csv

> 21.04s user 0.38s system 99% cpu 21.633 total ~164MB

### Lz4

- Bad compression

        time lz4 -1 sample-data.csv sample-data.csv.lz4

> 0.90s user 0.59s system 98% cpu 1.521 total ~276MB

- Mid compression

        time lz4 -4 sample-data.csv sample-data.csv.lz4

> 7.19s user 0.29s system 99% cpu 7.524 total ~190MB

- Best compression

        time lz4 -9 sample-data.csv sample-data.csv.lz4

> 9.93s user 0.28s system 99% cpu 10.232 total ~188MB

### 7zip (p7zip - lzma2)

- Bad compression

            time 7z a -t7z -m0=lzma2 -mx=1 -mfb=64 -md=32m -ms=on sample-data.csv.7z sample-data.csv
      
    > 240.26s user 1.03s system 269% cpu 1:29.66 total ~141MB      
            
            time 7z a -t7z -m0=lzma2 -mx=1 -ms=on sample-data.csv.7z sample-data.csv
            
    > 51.68s user 0.32s system 1065% cpu 4.878 total ~156MB


- Mid compression

            time 7z a -t7z -m0=lzma2 -mx=4 -mfb=64 -md=32m -ms=on sample-data.csv.7z sample-data.csv
      
    > 245.62s user 1.31s system 265% cpu 1:33.01 total ~141MB      
            
            time 7z a -t7z -m0=lzma2 -mx=4 -ms=on sample-data.csv.7z sample-data.csv
            
    > 240.34s user 2.00s system 974% cpu 24.857 total ~144.2MB

- Best compression

            time 7z a -t7z -m0=lzma2 -mx=9 -mfb=64 -md=32m -ms=on sample-data.csv.7z sample-data.csv
      
    > 451.18s user 4.51s system 398% cpu 1:54.49 total ~111.5MB      
            
            time 7z a -t7z -m0=lzma2 -mx=9 -ms=on sample-data.csv.7z sample-data.csv
            
    > 425.21s user 4.18s system 191% cpu 3:43.68 total ~110MB

- Extract compressed file

            7z e sample-data.csv.7z

    > 6.56s user 0.13s system 99% cpu 6.739 total

- Using Docker (alpine image)

    ```bash
    
    /tmp/test 
    
    time 7z a -t7z -m0=lzma2 -mx=9 -mfb=64 -md=32m -ms=on sample-data.csv.7z sample-data.csv
    
    7-Zip [64] 16.02 : Copyright (c) 1999-2016 Igor Pavlov : 2016-05-21
    p7zip Version 16.02 (locale=C.UTF-8,Utf16=on,HugeFiles=on,64 bits,6 CPUs Intel(R) Core(TM) i7-8750H CPU @ 2.20GHz (906EA),ASM,AES-NI)
    
    Scanning the drive:
    1 file, 359883594 bytes (344 MiB)
    
    Creating archive: sample-data.csv.7z
    
    Items to compress: 1
    
    
    Files read from disk: 1
    Archive size: 111543677 bytes (107 MiB)
    Everything is Ok
    real	2m 49.45s
    user	10m 22.02s
    sys	0m 21.59s
    
    ```

    > 6.56s user 0.13s system 99% cpu 6.739 total 115MB

## References

- [A Guide to JavaFaker](https://www.baeldung.com/java-faker)
- [Introduction to OpenCSV](https://www.baeldung.com/opencsv)
- [Spring Boot Console Application](https://www.baeldung.com/spring-boot-console-app)