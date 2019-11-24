from sklearn.datasets import load_digits
from openTSNE import TSNE
from matplotlib import pyplot as plt

digits = load_digits()
X, y = digits["data"], digits["target"]

embedding = TSNE().fit(X)
embedding[:5]

target_ids = range(len(digits.target_names))

plt.figure(figsize=(6, 5))
colors = 'r', 'g', 'b', 'c', 'm', 'y', 'k', 'w', 'orange', 'purple'
for i, c, label in zip(target_ids, colors, digits.target_names):
    plt.scatter(embedding[y == i, 0], embedding[y == i, 1], c=c, label=label)
plt.legend()
plt.show()