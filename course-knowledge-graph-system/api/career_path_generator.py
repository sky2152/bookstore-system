from typing import List, Dict, Any
from .neo4j_utils import get_db_session
from .llm_utils import get_llm_response

class CareerPathGenerator:
    def __init__(self):
        pass

    def _get_user_info(self, user_id: str) -> Dict[str, Any]:
        """Retrieve user's current skills and interests from Neo4j."""
        query = """
        MATCH (u:User {id: $user_id})
        OPTIONAL MATCH (u)-[:POSSESSES_SKILL]->(s:Skill)
        RETURN u.name AS name, u.interests AS interests, u.experience AS experience, COLLECT(s.name) AS current_skills
        """
        with get_db_session() as session:
            result = session.run(query, user_id=user_id).single()
            if result:
                return result.data()
            return {}

    def _get_career_goal_info(self, career_goal_id: str) -> Dict[str, Any]:
        """Retrieve career goal details and required skills from Neo4j."""
        query = """
        MATCH (cg:CareerGoal {id: $career_goal_id})
        OPTIONAL MATCH (cg)-[r:REQUIRES_SKILL]->(s:Skill)
        RETURN cg.name AS name, cg.description AS description, COLLECT({skill: s.name, importance: r.importance}) AS required_skills
        """
        with get_db_session() as session:
            result = session.run(query, career_goal_id=career_goal_id).single()
            if result:
                return result.data()
            return {}
            
    def _find_learning_resources_for_skill(self, skill_name: str) -> List[Dict[str, Any]]:
        """Find learning resources for a given skill from Neo4j."""
        query = """
        MATCH (lu:LearningUnit)-[:TEACHES]->(s:Skill {name: $skill_name})
        OPTIONAL MATCH (r:Resource)-[:OFFERS]->(lu)
        RETURN lu.name AS unit_name, lu.type AS unit_type, r.name AS resource_name, r.url AS resource_url
        """
        with get_db_session() as session:
            result = session.run(query, skill_name=skill_name)
            return [record.data() for record in result]

    def generate_path(self, user_id: str, career_goal_id: str) -> Dict[str, Any]:
        """
        Generates a personalized career development path.
        Combines user info, career goal, and LLM intelligence with KG data.
        """
        user_info = self._get_user_info(user_id)
        career_goal_info = self._get_career_goal_info(career_goal_id)

        if not user_info or not career_goal_info:
            return {"error": "User or Career Goal not found."}

        user_name = user_info.get("name", "用户")
        current_skills = ", ".join(user_info.get("current_skills", []))
        user_interests = ", ".join(user_info.get("interests", []))
        user_experience = ", ".join(user_info.get("experience", []))
        goal_name = career_goal_info.get("name", "未知目标")
        goal_description = career_goal_info.get("description", "")
        required_skills = career_goal_info.get("required_skills", [])

        # Filter out skills user already possesses
        missing_skills = [
            s["skill"] for s in required_skills 
            if s["skill"] not in user_info.get("current_skills", [])
        ]
        
        # Use LLM to generate a personalized learning path based on gathered info
        prompt = f"""
        你是一个专业的职业规划师和教育顾问。
        用户 \"{user_name}\" (兴趣: {user_interests}, 经验: {user_experience}, 已有技能: {current_skills})
        希望达成的职业目标是 \"{goal_name}\" ({goal_description})。
        为了达成这个目标，他需要掌握的技能包括：{r", ".join(missing_skills)}。

        请根据用户的背景和目标，以及缺失的技能，为用户制定一个3-5年的个性化学习和发展路径。
        路径应该包括：
        1. 详细的阶段性学习目标和建议（例如：第一阶段：掌握XX技能）。
        2. 为每个阶段建议2-3个具体的学习单元或任务（例如：完成在线课程XX，参与项目XX，考取认证XX）。
        3. 强调如何在AI时代保持人的不可替代性，并给出相关建议。
        请以结构化、易读的中文格式输出。
        """

        llm_path_suggestion = get_llm_response(prompt, model="qwen-plus") # Using qwen-plus for more detailed response

        # Enhance path with concrete resources from Neo4j based on LLM suggestions (simplified for now)
        detailed_path = []
        for skill in missing_skills:
            resources = self._find_learning_resources_for_skill(skill)
            if resources:
                detailed_path.append({
                    "skill": skill,
                    "suggested_resources": resources
                })

        return {
            "user_name": user_name,
            "career_goal": goal_name,
            "llm_path_suggestion": llm_path_suggestion,
            "detailed_resources_from_kg": detailed_path
        }

# Example usage (for testing purposes, can be removed later)
if __name__ == "__main__":
    generator = CareerPathGenerator()
    # Assuming user 'u1' and career goal 'cg1' exist in Neo4j
    user_id = "u1"
    career_goal_id = "cg1"
    
    print(f"Generating career path for user {user_id} towards goal {career_goal_id}...")
    path = generator.generate_path(user_id, career_goal_id)
    print(path)
