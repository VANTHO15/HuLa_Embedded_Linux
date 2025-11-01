#include <stdio.h>

extern int add(int a, int b);

int main() {
    int num1 = 10;
    int num2 = 5;

    printf("Addition: %d\n", add(num1, num2));
    return 0;
}
