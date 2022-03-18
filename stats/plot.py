import matplotlib.pyplot as plt
import numpy as np
from matplotlib.collections import EllipseCollection
import pandas as pd

df = pd.read_csv('../output.csv', sep=',')

x = df['x']
y = df['y']
size = df['r']+10
color = df['color']
cmap = plt.cm.hsv

print(size)

fig, ax = plt.subplots()
sc = ax.scatter(x, y, s=size, c=color, cmap=cmap)

offsets = list(zip(x, y))
for i, point in df.iterrows():
    ax.text(point['x'], point['y'], str(point['ID']))

plt.grid(color = 'grey', linestyle = '--', linewidth = 0.5)

annot = ax.annotate("", xy=(0,0), xytext=(20,20),textcoords="offset points",
                    bbox=dict(boxstyle="round", fc="w"),
                    arrowprops=dict(arrowstyle="->"))
annot.set_visible(False)

def update_annot(ind):
    index = int(ind["ind"])
    pos = sc.get_offsets()[ind["ind"][0]]
    annot.xy = pos
    text = "id: {}\nr: {}".format(index, size[index]-10)
    annot.set_text(text)

def hover(event):
    vis = annot.get_visible()
    if event.inaxes == ax:
        cont, ind = sc.contains(event)
        if cont:
            update_annot(ind)
            annot.set_visible(True)
            fig.canvas.draw_idle()
        else:
            if vis:
                annot.set_visible(False)
                fig.canvas.draw_idle()


fig.canvas.mpl_connect("motion_notify_event", hover)

plt.show()