import os
import dashscope
from dashscope.api_entities.dashscope_response import Message

# Set your DashScope API key from environment variables or direct assignment
# It is highly recommended to use environment variables for security
# For example: export DASHSCOPE_API_KEY='YOUR_API_KEY'
# or set it in a .env file and load it.
# For now, we'll use a placeholder.
DASHSCOPE_API_KEY = os.getenv("DASHSCOPE_API_KEY", "YOUR_DASHSCOPE_API_KEY")
dashscope.api_key = DASHSCOPE_API_KEY

def get_llm_response(prompt: str, model: str = "qwen-turbo") -> str:
    """
    Sends a prompt to the Tongyi Qianwen LLM and returns its response.

    Args:
        prompt (str): The input prompt for the LLM.
        model (str): The name of the Qwen model to use (e.g., "qwen-turbo", "qwen-plus").

    Returns:
        str: The LLM's generated response.
    """
    if not dashscope.api_key or dashscope.api_key == "YOUR_DASHSCOPE_API_KEY":
        return "DashScope API key is not configured. Please set DASHSCOPE_API_KEY."

    messages = [
        Message(role='system', content='You are a helpful assistant for career guidance and learning.'),
        Message(role='user', content=prompt)
    ]

    try:
        response = dashscope.Generation.call(
            model=model,
            messages=messages,
            seed=1234,  # You can change this for different random responses
            result_format='message',  # Set the result to be a message structure.
        )
        if response.status_code == 200:
            return response.output.choices[0].message.content
        else:
            return f"LLM request failed with status code {response.status_code}: {response.code} - {response.message}"
    except Exception as e:
        return f"An error occurred during LLM request: {e}"

# Example usage (for testing purposes, can be removed later)
if __name__ == "__main__":
    test_prompt = "请用中文介绍一下人工智能领域最近的职业趋势。"
    print(f"Querying LLM with prompt: '{test_prompt}'")
    response_content = get_llm_response(test_prompt)
    print("LLM Response:")
    print(response_content)

    test_prompt_2 = "给我推荐一个学习Python编程的初学者路线。"
    print(f"\nQuerying LLM with prompt: '{test_prompt_2}'")
    response_content_2 = get_llm_response(test_prompt_2)
    print("LLM Response:")
    print(response_content_2)
