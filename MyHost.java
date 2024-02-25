/* Implement this class. */

import java.util.Comparator;
import java.util.PriorityQueue;

public class MyHost extends Host {
	public int stop = 0;
	public Task  task = null;
	public long duration = 0;
	public long stopTask = 0;
	// Comparator for the Priority Queue that sorts the tasks descending by their priority,
	// and after that ascending by their id
	Comparator<Task> comp = Comparator
				.comparingInt((Task task) -> task.getPriority()).reversed()
				.thenComparingInt(Task::getId);
	PriorityQueue<Task> q = new PriorityQueue<>(comp);

	@Override
	public void run() {
		// Infinite loop, stops at shutdown
		while (stop == 0) {
			try {
				// If a task exists, execute it.
				if (task != null) {
					// If its duration reached 0, mark the task as finished.
					if (task.getLeft() == stopTask) {
						task.finish();
						// Eliminate the current task.
						task = null;
						if (q.size() != 0) {
							// If a task exists in queue, mark it as running(place in
							// variable).
							task = q.poll();
							// Execute the task
							task.setLeft(task.getLeft() - 1000);
							duration -= 1000;
						}
					} else {
						// Execute the current task
						task.setLeft(task.getLeft() - 1000);
						duration -= 1000;
					}
					// If there is a task running, it ran for 1 second, which can be
					// marked using sleep(1000).
					if (task != null) {
						Thread.sleep(1000);
					} else {
						// Waiting for arrival of new task.
						Thread.sleep(10);
					}
				} else {
					// Waiting for arrival of new task.
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addTask(Task task) {
		// If there is no task running, make the received one running.
		if (this.task == null) {
			this.task = task;
		} else {
			// Verify if the running task is preemptible.
			if (this.task.isPreemptible()) {
				// If the received task has greater priority, replace the current one
				// with the new one, and add it to the queue (it will be finished later).
				if (task.getPriority() > this.task.getPriority()) {
					q.add(this.task);
					this.task = task;
				} else {
					// Else, add the new task to the queue;
					q.add(task);
				}
			} else {
				// Add the received task to the queue.
				q.add(task);
			}
		}
		// Add the duration of execution of the received task to the total duration of
		// execution of all tasks by the host.
		duration += task.getDuration();
	}

	@Override
	public int getQueueSize() {
		// If a task is not running, send the size of the queue. Else, add 1 to the size.
		// (total tasks to be executed).
		if (task != null) {
			return 1 + q.size();
		}
		return q.size();
	}

	@Override
	public long getWorkLeft() {
		// Send the total time of execution for all tasks by the host. (remaining time)
		return duration;
	}

	@Override
	public void shutdown() {
		// Stop the program.
		stop = 1;
	}
}
