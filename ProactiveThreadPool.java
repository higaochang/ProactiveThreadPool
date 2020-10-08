public class ProactiveThreadPool{

	static public ExecutorService newProactiveThreadPool(int queueSize, int corePoolSize, int maximumPoolSize, final String poolName)
	{
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(queueSize)
		{
			@Override
			public boolean offer(Runnable e)
			{
				// fake full to proactively create worker thread to maximumPoolSize
				return false;
			}
		};

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.SECONDS, queue,
			new ThreadFactoryBuilder().setNameFormat(poolName + "-threadpool-%d").build(), (r, executor) ->
			{
		      	// number of threads reaches maximumPoolSize, now put task to queue
		      	// might be blocking on put() if queue was already full
				try
				{
					executor.getQueue().put(r);
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			});

		return threadPool;
	}

}
