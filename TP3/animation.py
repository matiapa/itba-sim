import sys

# Files
out_file = open('out.txt', "r")
ovito_file = open("out.xyz", "w")

# get first line of out_file
line = out_file.readline().rstrip().split(' ')
N = int(line[0])
L = int(line[1])
max_v = int(line[2])
delta_t = float(line[3])

# Write first line of ovito file
def write_first_lines(N, L):
    ovito_file.write(str(N+4))
    first_line = "\n\n0 0 0 1e-10 255 255 255\n0 0 {} 1e-10 255 255 255\n0 {} 0 1e-10 255 255 255\n0 {} {} 1e-10 255 255 255\n".format(L, L, L, L)
    ovito_file.write(first_line)

time = 0
target_time = 0
first = True

write_first_lines(N, L)

# iterate through out_file
for line in out_file:
    # New time iteration
    # Only add if time is greater than target time
    if line.rstrip() == "~":
        if first:
            first = False
            out_file.readline()
            continue
        if time >= target_time:
            target_time += delta_t
            if time >= target_time:
                print('The delta t is too big to generate animation.\nThere were no events during gap')
                exit()
            # Get time from next line and write to ovito file only if the time is greater than the target time
            time = float(out_file.readline().rstrip())
            if time >= target_time:
                write_first_lines(N, L)
        continue
    
    # Get particle data if time is greater than target time
    if time >= target_time:
        (id, x, y, vx, vy, r) = line.rstrip().split(' ')
        v_mod = (float(vx) * float(vx) + float(vy) * float(vy)) ** 0.5 # Calculate velocity magnitude
        # Set rgb color based on particle velocity magnitude and max velocity magnitude
        # If it is the big particle then set color to violet
        if int(id) == 0:
            r_color, g_color, b_color = 0, 0, 1
        else: 
            v = v_mod/max_v

            if v < 0.2:
                r_color, g_color, b_color = 0, 1, 0
            elif v < 0.4:
                r_color, g_color, b_color = 0.5, 1, 0
            elif v < 0.6:
                r_color, g_color, b_color = 1, 1, 0
            elif v < 0.8: 
                r_color, g_color, b_color = 1, 0.5, 0
            else:
                r_color, g_color, b_color = 1, 0, 0

        ln = str(int(id) + 1) + ' ' + str(x) + ' ' + str(y) + ' ' + str(r) + ' ' + str(r_color) + ' ' + str(g_color) + ' ' + str(b_color) + '\n'
        ovito_file.write(ln)

ovito_file.close()