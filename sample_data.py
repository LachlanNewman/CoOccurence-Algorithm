import os
from glob import glob

SAMPLE_SIZE = 1000


def createSampleData(output_directory, sample_output_directory):
    output_files = glob(output_directory)
    for file in output_files:
        num_line = 0
        file_name = file.split('\\').pop()
        data = open(file, 'r', encoding='utf-8')
        sample_data = open('{}\{}.txt'.format(sample_output_directory, file_name), 'w', encoding='utf-8')
        while num_line < SAMPLE_SIZE:
            line = data.readline()
            sample_data.write(line)
            num_line = num_line + 1
        data.close()
        sample_data.close()


def readIMCB():
    # read Strips output
    OUTPUT_DIRECTORY = 'InMapCombining\Stripes\output\*'
    SAMPLE_OUTPUT_DIRECTORY = 'InMapCombining\Stripes\sample_ouput'
    createSampleData(OUTPUT_DIRECTORY,SAMPLE_OUTPUT_DIRECTORY)

def readStripes():
    OUTPUT_DIRECTORY = 'Stripes\output\*'
    SAMPLE_OUTPUT_DIRECTORY = 'Stripes\sample_output'
    createSampleData(OUTPUT_DIRECTORY,SAMPLE_OUTPUT_DIRECTORY)

def readTripData():
    PAIRS_OUTPUT_DIRECTORY ='TripData\Pairs\output\*'
    STRIPES_OUTPUT_DIRECTORY = 'TripData\Stripes\output\*'
    PAIRS_SAMPLE_OUTPUT_DIRECTORY = 'TripData\Pairs\sample_output'
    STRIPES_SAMPLE_OUTPUT_DIRECTORY = 'TripData\Stripes\sample_output'
    createSampleData(PAIRS_OUTPUT_DIRECTORY,PAIRS_SAMPLE_OUTPUT_DIRECTORY)
    createSampleData(STRIPES_OUTPUT_DIRECTORY,STRIPES_SAMPLE_OUTPUT_DIRECTORY)

def readPairs():
    OUTPUT_DIRECTORY = 'Pairs\output\*'
    SAMPLE_OUTPUT_DIRECTORY = 'Pairs\sample_output'
    createSampleData(OUTPUT_DIRECTORY, SAMPLE_OUTPUT_DIRECTORY)

def readReducers():
    reducers = [3, 6, 9, 12, 15, 18, 21, 24, 27]
    for reducer in reducers:
        OUTPUT_DIRECTORY = 'Reducers\output\{}reducers\*'.format(reducer)
        SAMPLE_OUTPUT_DIRECTORY = 'Reducers\sample_output\{}reducers'.format(reducer)
        createSampleData(OUTPUT_DIRECTORY, SAMPLE_OUTPUT_DIRECTORY)


if __name__ == '__main__':
    # read InMapCombining
    readIMCB()
    # read pairs
    readPairs()
    # read reducers
    readReducers()
    # read stripes
    readStripes()
    # read trip data
    readTripData()
