# Compiler and flags
CC := ${CC}
CFLAGS := ${CFLAGS}

# Source files
MATH_LIB_SRC := math-lib.c
ADD_SRC := add_1.0.c

# Object files
MATH_LIB_OBJ := math-lib.o
ADD_OBJ := add_inter.o

# Binary output
TARGET := add

all: $(TARGET)

$(TARGET): $(MATH_LIB_OBJ) $(ADD_OBJ)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@ -Wl,--hash-style=gnu

$(MATH_LIB_OBJ): $(MATH_LIB_SRC)
	$(CC) $(CFLAGS) -c $< -o $@

$(ADD_OBJ): $(ADD_SRC)
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f $(MATH_LIB_OBJ) $(ADD_OBJ) $(TARGET)
