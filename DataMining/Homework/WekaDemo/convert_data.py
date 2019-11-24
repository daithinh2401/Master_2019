# -*- coding: utf-8 -*-

data = open('plants.data', 'r', encoding="ISO-8859-1")
output = open('plants.csv', 'w')

def load_stateabbr():
    dict_state = {}
    f = open('stateabbr.txt')
    for line in f:
        state = line.strip().split(" ")[0]
        dict_state[state] = "n"

    f.close()
    return dict_state

states_origin = load_stateabbr()
states = states_origin.copy()

header = "name"
for s in states:
    header += "," + s
output.write(header + "\n")    
for line in data:
    plant = line.strip().split(",")
    plant_name = plant[0]
    new_line = plant_name

    for i in range(1, len(plant)):
        if plant[i] in states:
            states[plant[i]] = "y"

    for s in states:
        new_line += "," + str(states[s])

    output.write(new_line + "\n")
    states = states_origin.copy()
data.close()
output.close()