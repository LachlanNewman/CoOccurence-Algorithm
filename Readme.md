Steps to run the program
1) Navigate to the root directory : lachlaninmapcombine
2) run: mvn package
3) Upload to main node on hadoop th efollowing files
    1. target/lachlan.inmapcombine-1.0-SNAPSHOT.jar to hadoop main node
4) navigate to main node
5) run the following command to run MapReduce Pairs:
    hadoop jar lachlan.inmapcombine-1.0-SNAPSHOT.jar lachlan.Pairs {inputfile} {outputfile} {num} {imcb} {text}
    num = number of reducers ie 3, 4 should be an integer but not negative
    imcb = true to perform in map combining otherwise should = false
    text = true to perform map reduce on a non warc.wet.gz file other wise should = false
5) run the following command to run MapReduce Stripes:
    hadoop jar lachlan.inmapcombine-1.0-SNAPSHOT.jar lachlan.Stripes {inputfile} {outputfile} {num} {imcb} {text}
    num = number of reducers ie 3, 4 should be an integer but not negative
    imcb = true to perform in map combining otherwise should = false
    text = true to perform map reduce on a non warc.wet.gz file other wise should = false

Example:
    hadoop jar lachlan.inmapcombine-1.0-SNAPSHOT.jar lachlan.Stripes /user/s3691320/file.warc.wet.gz /stripesout 3 true false
    hadoop jar lachlan.inmapcombine-1.0-SNAPSHOT.jar lachlan.Pairs /user/s3691320/file.txt /pairsout 10 false true

Note: in map combining is not available when processing a text file and should always be false ie the following will cause an error
     hadoop jar lachlan.inmapcombine-1.0-SNAPSHOT.jar lachlan.Pairs /user/s3691320/file.txt /pairsout 10 false false