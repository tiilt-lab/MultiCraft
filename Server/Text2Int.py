from word2number import w2n
import spacy
from spacy.lang.en import English
nlp = spacy.load('en_core_web_sm')


def text2int(textnum):
  num=""
  #use spacy to recognize numerical parts of the text and concatenate them
  
  spacy_ent=nlp(textnum)
  for ent in spacy_ent.ents:
      if ent.label_=='CARDINAL':
          num+=ent.text+" "


  # use word2number to convert to integer

  result=w2n.word_to_num(num)
  return result
    