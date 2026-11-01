# Measurement protocol

Record environment, commit SHA, corpus and query set, concurrency and arrival rate, warm-up, duration, raw samples, error counts, and bottleneck for each reproducible run. Report p50/p95/p99, throughput and time to first token, with sample count. Quality evaluation should report recall@5/10, MRR, nDCG@10 and refusal accuracy on a documented held-out set; faithfulness requires an independently calibrated judge. Compare configurations on the same set. Test tenant isolation and failure recovery separately.

AWS projections must state instance types, region, model, quotas, workload, assumptions and uncertainty. They are estimates, not measurements or proof of deployment. No numbers are currently available.
