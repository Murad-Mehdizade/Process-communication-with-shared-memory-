# Process-communication-with-shared-memory

- SequentialHistogramService: The problem with sequentiality has been solved.
- LowlevelHistogramService: Low-level structures such as synchronised, volatile, wait() and notify() are used for synchronisation.
- HighlevelHistogramService: Only uses high-level constructs such as BlockingQueue, Semaphore, SynchronisedLinkedList and etc. for synchronisation.
