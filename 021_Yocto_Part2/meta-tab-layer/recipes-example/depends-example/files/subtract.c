#include <stdio.h>

extern int subtract(int a, int b);

int main() {
    int num1 = 10;
    int num2 = 5;

    printf("subtract: %d\n", subtract(num1, num2));
    return 0;
}
