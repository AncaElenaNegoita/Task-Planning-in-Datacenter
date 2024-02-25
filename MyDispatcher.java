/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
	public int id = 0;

	public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
		super(algorithm, hosts);
	}

	@Override
	public void addTask(Task task) {
		// Synchronizing in order to not overlap 2 tasks
		synchronized(this) {
			// Verify what algorithm is selected
			if (SchedulingAlgorithm.ROUND_ROBIN == algorithm) {
				// Send to the host of id (last_id + 1) % host_size.
				hosts.get(id++ % hosts.size()).addTask(task);
			} else if (SchedulingAlgorithm.SHORTEST_QUEUE == algorithm) {
				// Store the first size of queue of the first host.
				MyHost host = (MyHost)hosts.get(0);
				int minQ = host.getQueueSize();
				int index = 0;
				for (int i = 1; i < hosts.size(); i++) {
					// If a smaller size is found, replace the minimum and the index of
					// the host.
					host = (MyHost)hosts.get(i);
					if (host.getQueueSize() < minQ) {
						index = i;
						minQ = host.getQueueSize();
					}
				}
				// Send the task to the host with the smallest queue size.
				hosts.get(index).addTask(task);
			} else if (SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT == algorithm) {
				// Depending on the type of the task, send each task to its specific host.
				if (task.getType() == TaskType.SHORT) {
					hosts.get(0).addTask(task);
				} else if (task.getType() == TaskType.MEDIUM) {
					hosts.get(1).addTask(task);
				} else if (task.getType() == TaskType.LONG) {
					hosts.get(2).addTask(task);
				}
			} else if (SchedulingAlgorithm.LEAST_WORK_LEFT == algorithm) {
				// Store the total duration(runtime) of the first host in order to
				// execute all its tasks.
				MyHost host = (MyHost)hosts.get(0);
				long minWL = host.getWorkLeft();
				int index = 0;
				for (int i = 1; i < hosts.size(); i++) {
					// If there is a host with less duration remaining, replace the
					// minimum and the index of the host.
					host = (MyHost)hosts.get(i);
					if (host.getWorkLeft() < minWL) {
						index = i;
						minWL = host.getWorkLeft();
					}
				}
				// Send the task to the host with the smallest duration of execution 
				// remaining.
				hosts.get(index).addTask(task);
			}
		}
	}
}
