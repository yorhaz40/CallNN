# CallNN
Code for paper A Neural-Network based Code Summaization Approach by Using Source Code and its Call Dependencies

# Extract Call dependency tool

## Usage
This tool is used to extract <code, comment, call dependency sequence> data from java projects.

To use this tool, run "java -jar jtags.jar --project-path --output-path"

Project-path is the project you want to analyse, and output-path is the extracted data to output to.

The result of this tool is 4 files, named as "code.data, comment.data, seq.data, tuple.json". Every line in the first three files has the format "id\tdata", and the ids of these files are related.

The "tuple.json" file is not used in our experiment. It save the source code and the entire related codes it called. 

# Data preparation

The data should be cleaned before training. We use python to do data clean process.
To get the training data, do follows:
1. put code.data seq.data and comment.data in the same folder of python source code.
2. run callnn.py to get formated seq data, its name is formatseq.data
3. remove the original "seq.data" and rename the "formatseq.data" to "seq.data", then run dataprocess.py

Our data can be found in the folder "call".

# Training model

the model is based on  https://github.com/eske/seq2seq and https://github.com/xing-hu/TL-CodeSum

1. put the prepared data into right position
2. The configuration of different models that we used is available in the folder "config", we use "call.yaml" in our experiment.

3. run "python3 main.py ../config/**.yaml --train -v" to train the model.
