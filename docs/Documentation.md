# Ninestars QA Suite

The Ninestars QA Suite consists of two tools for checking the newspaper scans for The State and University Library in
Aarhus, Denmark.

One tool is designed to be run on a single JPEG 2000 file, and will report any deviations from the specification of the
file. The other tool is designed to be run on a directory structure containing the output of an entire batch, including
data and metadata, and will report any deviations from the specifications.

## Prerequisites

The QA Suite is a work in progress, and the list of prerequisites can not yet be fully described.

Prerequisites will include:

* A linux system. The tool has been tested on [CentOS][1] release 6.4, but most linux versions should work, provided the
    other prerequisites can be met.
* [Java SE 7][2]
* [Jpylyzer][3]

This list will probably be extended.

## Running

### The file tool

Start the file tool with

```
bin/qafile.sh <path-to-file>
```

`<path-to-file>` is the path to the file to check.

### The batch tool

Start the batch tool with

```
bin/qabatch.sh <path-to-batch> <sql-connection-string>
```

`<path-to-batch>` is the path to the directory containing the batch to check.

`<sql-connection-string>` is the connection string used to connect to the database with the information about the batch.

The database contains information about the data in a batch, and a reference to documentation about the database will be inserted, when it is ready.

## Output

Both tools will output an XML file to stdout, containing the result of the validation (success or failure) and a list of
errors, if any.

The schema for the XML file can be found here [Schema](xsd/qaresult).

Examples of output can be found here: [Success](examples/qaresult-success-example.xml)
and [Failure](examples/qaresult-failure-example.xml)

The QA Suite is a work in progress, and the schema and examples may be extended as a result of this.

If the tool fails unexpectedly, it will exit with an error code larger than zero, and error messages will be printed to
stderr.


[1]: http://www.centos.org
[2]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[3]: http://openplanetsfoundation.org/software/jpylyzer