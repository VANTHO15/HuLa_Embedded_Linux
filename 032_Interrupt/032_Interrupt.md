# 💚 Interrupt 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã biết về timer. Nếu các bạn chưa đọc thì xem link này nha [031_Timer.md](../031_Timer/031_Timer.md). Ở bài này chúng ta sẽ tìm hiểu về Interrupt nhé.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Timer](#1️⃣-timer)
    - [2. Thực hành](#2️⃣-thực-hành)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ Interrupt
***Tại sao chúng ta cần ngắt?***
+ Lấy thông tin từ phần cứng khi cần (Hardware Interrupts)
+ Xử lý dữ liệu khi cần (Software Interrupts)
+ Khi ta dùng polling: Tốn kém do kiểm tra không cần thiết
+ Giải pháp tốt hơn: Ngắt khi này công việc chỉ được thực hiện khi cần thiết
+ Ngắt được tạo ra một cách vật lý bởi các tín hiệu điện tử bắt nguồn từ các hardware devices và được dẫn đến các chân đầu vào trên interrupt controller.
+ Khi receiving 1 interrupt, interrupt controller sẽ gửi tín hiệu đến processor. Processor phát hiện signal này và interrupts quá trình thực thi hiện tại để xử lý ngắt.
+ Sau đó, processor có thể thông báo cho operating system rằng một ngắt đã xảy ra, và operating system có thể xử lý ngắt một cách thích hợp.
+ Sử dụng một giá trị duy nhất được liên kết với mỗi đầu ra để phân biệt ngắt với nhiều nguồn khác nhau.
+ Interrupt value often called interrupt request line (IRQ).

***API***
```c
static irqreturn_t intr_handler(int irq,     
                                void *dev_id,       
                                struct pt_regs *regs) 
 
int request_irq(unsigned int irq,                                      
                irqreturn_t (*handler)(int, void *, struct pt_regs *), 
				unsigned long irqflags,                                
				const char *devname,                                                   
				void *dev_id)
```

***Interrupt Context***
+ Không liên kết với bất kỳ tiến trình nào
+ Tiến trình đang chạy sẽ được đưa vào bộ nhớ kernel
+ Quy tắc đơn giản:
	+ Nhanh chóng và ngắn gọn
	+ Chỉ sử dụng ngăn xếp nếu thực sự cần thiết
+ Tất cả các tiến trình trên CPU đều bị chặn bởi Ngắt
+ Chỉ được sử dụng cho các tác vụ quan trọng về thời gian và xác nhận thời điểm đến
+ Quá trình xử lý ngắt được chia thành hai phần, hay còn gọi là hai nửa.
+ Nửa trên(Top halves) được sử dụng cho các tác vụ quan trọng về thời gian. Cần nhanh chóng, đơn giản và chạy với một số hoặc tất cả các ngắt bị vô hiệu hóa.
+ Nửa dưới(Bottom halves) được sử dụng cho các tác vụ ít quan trọng hơn, chạy trong tương lai, vào thời điểm thuận tiện hơn. Bật ngắt.
+ Sử dụng softirq, Tasklet, Work-Queue để triển khai Nửa dưới.

+ Ví dụ: Màn hình sẽ bật khi chạm vào và tắt sau 5 giây không hoạt động. => Ngắt để phát hiện cảm ứng chạm và hàng đợi công việc để ghi lại tác vụ tắt màn hình

### 2️⃣ Thực hành
+ Này là ví dụ để đọc thôi, chứ ngắt thì phải chạy trên mạch. Mình sẽ làm tiếp ở bài phía sau để chạy ngắt gpio trên mạch nhé.

+ File gpio_interrupt.c
```c
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/interrupt.h>
#include <linux/gpio.h>
#include <linux/errno.h>

#define GPIO_ANY_GPIO                28
#define GPIO_ANY_GPIO_DESC           "Some gpio pin description"
#define GPIO_ANY_GPIO_DEVICE_DESC    "some_device"

static int irq_any_gpio = 0;

/* IRQ handler: new prototype for kernel >= 4.1 */
static irqreturn_t gpio_irq_handler(int irq, void *dev_id)
{
    unsigned long flags;

    /* Disable hard interrupts (save state) */
    local_irq_save(flags);
    printk(KERN_INFO "[GPIO irq] Interrupt [%d] for device %s was triggered!\n",
           irq, (char *)dev_id);
    /* Restore interrupt state */
    local_irq_restore(flags);

    return IRQ_HANDLED;
}

static int __init gpio_irq_init(void)
{
    int ret = 0;

    printk(KERN_INFO "[GPIO irq] Initializing module...\n");

    /* Request the GPIO */
    ret = gpio_request(GPIO_ANY_GPIO, GPIO_ANY_GPIO_DESC);
    if (ret) {
        printk(KERN_ERR "[GPIO irq] GPIO request failure: %s\n", GPIO_ANY_GPIO_DESC);
        return ret;
    }

    /* Set direction (input) */
    ret = gpio_direction_input(GPIO_ANY_GPIO);
    if (ret) {
        printk(KERN_ERR "[GPIO irq] GPIO direction failure: %s\n", GPIO_ANY_GPIO_DESC);
        gpio_free(GPIO_ANY_GPIO);
        return ret;
    }

    /* Map GPIO to IRQ */
    irq_any_gpio = gpio_to_irq(GPIO_ANY_GPIO);
    if (irq_any_gpio < 0) {
        printk(KERN_ERR "[GPIO irq] GPIO to IRQ mapping failure for %s\n", GPIO_ANY_GPIO_DESC);
        gpio_free(GPIO_ANY_GPIO);
        return irq_any_gpio;
    }

    printk(KERN_INFO "[GPIO irq] Mapped GPIO %d to IRQ %d\n", GPIO_ANY_GPIO, irq_any_gpio);

    /* Request IRQ */
    ret = request_irq(irq_any_gpio,
                      gpio_irq_handler,          /* new style handler */
                      IRQF_TRIGGER_FALLING | IRQF_TRIGGER_RISING,
                      GPIO_ANY_GPIO_DESC,
                      GPIO_ANY_GPIO_DEVICE_DESC);
    if (ret) {
        printk(KERN_ERR "[GPIO irq] IRQ request failure: %d\n", ret);
        gpio_free(GPIO_ANY_GPIO);
        return ret;
    }

    printk(KERN_INFO "[GPIO irq] Module loaded successfully\n");
    return 0;
}

static void __exit gpio_irq_exit(void)
{
    free_irq(irq_any_gpio, GPIO_ANY_GPIO_DEVICE_DESC);
    gpio_free(GPIO_ANY_GPIO);
    printk(KERN_INFO "[GPIO irq] Module unloaded\n");
}

module_init(gpio_irq_init);
module_exit(gpio_irq_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Nguyen Van Tho");
MODULE_DESCRIPTION("GPIO interrupt example for kernel 5.4+");
```

+ File Makefile
```Makefile
obj-m += gpio_interrupt.o
KERNELDIR ?= /lib/modules/$(shell uname -r)/build

all:
	$(MAKE) -C $(KERNELDIR)  M=$(PWD) modules
clean:
	$(MAKE) -C $(KERNELDIR)  M=$(PWD) clean
```

​<p align="center">
  <img src="Images/Screenshot_5.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

## ✔️ Conclusion
Ở bài này chúng ta đã biết về interrupt. Tiếp theo chúng ta sẽ tìm hiểu về Kernel Synchronization nhé.


## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] https://docs.kernel.org/filesystems/proc.html

[3] Linux Device Drivers 3rd Edition (LDD3)

[4] https://www.linux.com/training-tutorials kernel-newbie-corner-kernel-debugging-using-proc-sequence-files-part-1/
