# 💚 Character Driver Multi Device 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã biết và tạo ra được proc filesystem. Nếu các bạn chưa đọc thì xem link này nha [030_Proc_Filesystem.md](../030_Proc_Filesystem/030_Proc_Filesystem.md). Ở bài này chúng ta sẽ tìm hiểu về timer và interrupt nhé.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Proc filesystem](#1️⃣-proc-filesystem)
    - [2. Thực hành](#2️⃣-thực-hành)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ Timer
+ Nhiều tình huống trong kernel sẽ sử dụng tới timer như việc viết kernel theo kiểu điều khiển bằng thời gian hoặc theo kiểu điều khiển về sự kiện. Khi này ta cần tạo ra các khoảng thời gian(perior). Rồi delay để lên lịch cho công việc tiếp theo
+ Khi này kernel sử dụng hardware timer để đo thời gian. Chu kỳ của hardware timer là một tick
+ Frequency của system timer (the tick rate) là HZ. Khi này ta sẽ có 1 biến toàn cục **jiffies** để lưu trữ số tích tắc đã xảy ra kể từ khi hệ thống khởi động.
+ Ví dụ
```c
unsigned long later = jiffies + 5*HZ;                   /* five seconds from now */
unsigned long fraction = jiffies + HZ / 10;             /* 1/10 second from now
```

***Ví dụ tạo delay trong proc file***
+ Bài này gồm 2 file là Delay_Kernal.c và Makefile

+ File Delay_Kernal.c
```c
#include <linux/module.h>
#include <linux/moduleparam.h>
#include <linux/init.h>
#include <linux/time.h>
#include <linux/timer.h>
#include <linux/kernel.h>
#include <linux/proc_fs.h>
#include <linux/types.h>
#include <linux/spinlock.h>
#include <linux/interrupt.h>
#include <linux/sched.h>

#include <linux/slab.h>
#include <linux/seq_file.h>

#include <asm/hardirq.h>

/*
 * This module is a silly one: it only embeds short code fragments
 * that show how time delays can be handled in the kernel.
 */

int delay = HZ; /* the default delay, expressed in jiffies */

module_param(delay, int, 0);

MODULE_AUTHOR("Alessandro Rubini");
MODULE_LICENSE("Dual BSD/GPL");


/* use these as data pointers, to implement four files in one function */
enum jit_files {
	JIT_BUSY,
	JIT_SCHED,
	JIT_QUEUE,
	JIT_SCHEDTO
};

/*
 * This function prints one line of data, after sleeping one second.
 * It can sleep in different ways, according to the data pointer
 */

static int jit_fn(struct seq_file *m, void* arg)
{
	unsigned long j0, j1;  /* jiffies */
	wait_queue_head_t wait;

	init_waitqueue_head(&wait);
	j0 = jiffies;
	j1 = j0 + delay;

	switch ((long)arg) {
	case JIT_BUSY:
		while (time_before(jiffies, j1))
			cpu_relax();
		break;
	case JIT_SCHED:
		while (time_before(jiffies, j1))
			schedule();
		break;
	case JIT_QUEUE:
		wait_event_interruptible_timeout(wait, 0, delay);
		break;
	case JIT_SCHEDTO:
		set_current_state(TASK_INTERRUPTIBLE);
		schedule_timeout (delay);
		break;
	}

	j1 = jiffies; /* actual value after we delayed*/
	seq_printf(m, "%9li %9li", j0, j1);

	return 0;
}

static int jit_fn_open(struct inode *inode, struct file *file)
{
	return single_open(file, jit_fn, NULL);
}

static const struct file_operations jit_fn_fpos = {
	.owner = THIS_MODULE,
	.open  = jit_fn_open,
	.read  = seq_read,
	.llseek = seq_lseek,
	.release = single_release,
};



/*
 * This file, on the other hand, returns the current time forever
 */
static int jit_currentime (struct seq_file *file, void* arg)
{
	struct timeval tv1;
	struct timespec tv2;
	unsigned long j1;
	u64 j2;

	/* get them four */
	j1 = jiffies;
	j2 = get_jiffies_64();
	do_gettimeofday(&tv1);
	tv2 = current_kernel_time();

	/* print */
	seq_printf(file, "0x%08lx 0x%016Lx %10i.%06i\n"
	           "%40i.%09i\n",
	           j1, j2,
	           (int) tv1.tv_sec, (int) tv1.tv_usec,
	           (int) tv2.tv_sec, (int) tv2.tv_nsec);
	return 0;
}

static int jit_currentime_open(struct inode *inode, struct file *file)
{
	return single_open(file, jit_currentime, NULL);
}

static const struct file_operations jit_currentime_fpos = {
	.owner = THIS_MODULE,
	.open  = jit_currentime_open,
	.read  = seq_read,
	.llseek = seq_lseek,
	.release = single_release,
};


int __init jit_init(void)
{
	proc_create("jitcurrent", 0, NULL, &jit_currentime_fpos);

	proc_create_data("jitbusy",    0, NULL, &jit_fn_fpos, (void*)JIT_BUSY);
	proc_create_data("jitsched",   0, NULL, &jit_fn_fpos, (void*)JIT_SCHED);
	proc_create_data("jitqueue",   0, NULL, &jit_fn_fpos, (void*)JIT_QUEUE);
	proc_create_data("jitschedto", 0, NULL, &jit_fn_fpos, (void*)JIT_SCHEDTO);

	return 0; /* success */
}

void __exit jit_cleanup(void)
{
	remove_proc_entry("jitcurrent", NULL);
	remove_proc_entry("jitbusy"   , NULL);
	remove_proc_entry("jitsched"  , NULL);
	remove_proc_entry("jitqueue"  , NULL);
	remove_proc_entry("jitschedto", NULL);
}

module_init(jit_init);
module_exit(jit_cleanup);
```

+ File Makefile
```Makefile
obj-m += Delay_Kernal.o

KERNELDIR ?= /lib/modules/$(shell uname -r)/build

all:
	$(MAKE) -C $(KERNELDIR)  M=$(PWD) modules
clean:
	$(MAKE) -C $(KERNELDIR)  M=$(PWD) clean
```

+ Cách chạy:
    + 

## ✔️ Conclusion
Ở bài này chúng ta đã biết cách tạo 1 proc file system. Tiếp theo chúng ta sẽ tìm hiểu về timer để tạo delay và interrupt nhé.


## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] https://docs.kernel.org/filesystems/proc.html
