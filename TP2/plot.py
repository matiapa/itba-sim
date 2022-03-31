import pandas as pd
import matplotlib.pyplot as plt

L = 100
x_center = L/2
y_center = L/2

df = pd.read_csv('output.csv')
df.set_index(['rule'])

it_alive_cells = list()
it_radius = list()

def distance_to_center(x, y):
    return ((x - x_center)**2 + (y - y_center)**2)**0.5

rule_groups = df.groupby(df.columns[0])

processed_data = pd.DataFrame(columns=["rule", "t", "alive_cells", "radius"])

for name, group in rule_groups:
    # print("Running group: " + name)
    it_groups = group.groupby(group.columns[1])
    for it, it_group in it_groups:
        # print("Running iteration: " + str(it))
        count = 0
        max = 0
        for row in it_group.values:
            if (row[4] == 1):
                count += 1
            radius = distance_to_center(row[2], row[3])
            if (radius > max):
                max = radius
        processed_data.loc[len(processed_data)] = [name, it, count/100, max/100]

# processed_data.to_csv('processed_data.csv')

fig, (ax1, ax2) = plt.subplots(2)
for key, grp in processed_data.groupby(['rule']):
    # set x axes labels to plot
    ax1.set_xlabel("Iteration")
    ax1.set_ylabel("Alive Cells")
    ax1 = grp.plot(ax=ax1, kind='line', x='t', y='alive_cells', label=key)
    ax2.set_xlabel("Iteration")
    ax2.set_ylabel("Radius")
    ax2 = grp.plot(ax=ax2, kind='line', x='t', y='radius', label=key)



plt.show()
