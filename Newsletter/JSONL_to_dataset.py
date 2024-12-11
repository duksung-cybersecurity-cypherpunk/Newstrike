import jsonlines
from datasets import Dataset
import warnings
warnings.filterwarnings('ignore')

dataset = []
with jsonlines.open("") as f:
    for line in f.iter():
      dataset.append(f'<s>### Instruction: \n{line["inputs"]} \n\n### Response: \n{line["response"]}</s>')


# 데이터셋 생성 및 저장
dataset = Dataset.from_dict({"text": dataset})
dataset.save_to_disk('')