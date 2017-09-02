#!/usr/bin/env python
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
import re
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib
import pickle
import sys

def convert_review_words(base_review):
    review_letters = re.sub("[^a-zA-Z]", " ", base_review)
    review_letters = re.sub(r'\b\w{1,2}\b', '', review_letters)
    review_lower = review_letters.lower();
    review_words = review_lower.split()
    words = [w for w in review_words if not w in stopwords.words("english")]
    return (" ".join(words))

clean_test_reviews = []

clean_test_reviews.append(convert_review_words(sys.argv[1]))

vec = joblib.load('vec_count.joblib')

test_data_features = vec.transform(clean_test_reviews)

test_data_features = test_data_features.toarray()

forest_model = joblib.load('model.pkl')

result = forest_model.predict(test_data_features)

print result[0]