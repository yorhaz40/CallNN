import sys

import tensorflow as tf


from tensorflow.python.tools import inspect_checkpoint as chkp





if __name__ == "__main__":
    file = "/home/bohong/文档/seqmodel/TL-CodeSum/data/model/single_api/checkpoints/translate-76222"
    embedding_var = chkp.print_tensors_in_checkpoint_file(file, tensor_name="", all_tensors=True, all_tensor_names=True)
    ckpt = tf.train.get_checkpoint_state(file)
    # embedding = tf.Variable(embedding_var,"embedding")
    print(embedding_var)