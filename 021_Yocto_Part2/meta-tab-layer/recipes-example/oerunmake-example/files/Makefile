# Compiler and flags
CC := ${CC}
CFLAGS := ${CFLAGS}

TARGET ?= ''
libs ?= ''
MAIN_SRC ?= $(TARGET).c

# Source files
SRCS := $(MAIN_SRC) $(libs)

# Object files
OBJS := $(SRCS:.c=.o)

# Binary output

all: $(TARGET)

$(TARGET): $(OBJS) 
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@ -Wl,--hash-style=gnu

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@


install:
	install -d ${DESTDIR}${BINDIR}
	install -m 0755 ${TARGET} ${DESTDIR}${BINDIR}

clean:
	rm -f $(OBJS) $(TARGET)
