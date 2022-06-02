cython --embed main.py
gcc main.c -o main.out -I/usr/include/python3.10 -lpython3.10
./main.out
rm -rf __pycache__ main.c main.out