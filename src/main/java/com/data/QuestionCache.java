package com.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.entity.Question;
import com.utils.redis.AbstractRedisUtil;

public class QuestionCache {
	
	private static QuestionCache INSTANCE ;
	public static QuestionCache getInstance() {
		if(INSTANCE==null) {
			synchronized (QuestionCache.class) {
				if(INSTANCE==null) {
					INSTANCE = new QuestionCache();
				}
			}
		}
		return INSTANCE;
	}
	
	private static final String REDIS_KEY_QUESTION_SCORE_PER = "questions";
	private static final String REDIS_KEY_USER_QUESTION_PER = "userQuestions";
	private static final String REDIS_KEY_OLD_QUESTION_PER = "oldQuestions";
	private static final String REDIS_KEY_QUESTION_CACHE_PER = "questionCaches";

	private String getQuestionsKey(int userId, String subject) {
		return String.format("%s:%s:%s", REDIS_KEY_QUESTION_SCORE_PER, userId, subject);
	}	
	private String getUserQuestionKey(int userId, String subject) {
		return String.format("%s:%s:%s", REDIS_KEY_USER_QUESTION_PER, userId, subject);
	}	
	private String getOldQuestionKey(int userId, String subject) {
		return String.format("%s:%s:%s", REDIS_KEY_OLD_QUESTION_PER, userId, subject);
	}	
	private String getQuestioonCachesKey(int userId, String subject) {
		return String.format("%s:%s:%s", REDIS_KEY_QUESTION_CACHE_PER, userId, subject);
	}
	
	private static final int QUESTION_TIMEOUT_TIME = 30*60;
	
	private Map<String, Map<Integer, Question>> subjects = new HashMap<>();
	
	public Map<Integer, Question> getQuestions(String subject) {
		return subjects.get(subject);
	}
	
	public Question getQueestion(String subject, int id) {
		return subjects.get(subject).get(id);
	}

	public List<Integer> getUserQuestionCache(int userId, String subject){
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		String key = getQuestioonCachesKey(userId, subject);
		List<Integer> ids = redisUtil.lrange(key, 0, -1).stream().collect(
				ArrayList::new, (list, idStr)->{list.add(Integer.valueOf(idStr));},ArrayList::addAll);
		return ids;
	}
	
	public List<Integer> getOldQuestions(int userId, int size, String subject){
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		Set<String> questionIdsTmp = redisUtil.smembers(getOldQuestionKey(userId, subject));
		Set<String> questionIds = redisUtil.smembers(getOldQuestionKey(userId, subject));
		int i=0;
		for(String id : questionIdsTmp) {
			if(i==size-1) {
				break;
			}
			questionIds.add(id);
		}
		return questionIds.stream().collect(ArrayList::new, (list, idStr)->{list.add(Integer.valueOf(idStr));}, ArrayList::addAll);
	}
	
	public void removeOldQuestion(int userId, String subject, int questionId) {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		redisUtil.srem(getOldQuestionKey(userId, subject), Integer.toString((questionId)));
	}
	
	public void addOldQuestion(int userId, String subject, int questionId) {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		redisUtil.sadd(getOldQuestionKey(userId, subject), Integer.toString(questionId));
	}
	
	public List<Integer> getLongestQuestions(int userId, int size, String subject) {
		return getLongestQuestions(userId, 0, size, subject);
	}
	
	public List<Integer> getLongestQuestions(int userId, int start, int size, String subject) {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		Set<String> questionIds = redisUtil.zrange(getQuestionsKey(userId, subject), start, size);
		List<Integer> res = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		List<Integer> tmp = new ArrayList<Integer>();
		for(String questionId : questionIds) {
			res.add(Integer.valueOf(questionId));
			tmp.add(Integer.valueOf(questionId));
		}
		Collections.sort(tmp);
		for(int i : tmp) {
			builder.append(i+", ");
		}
		System.out.println(this.getClass().getSimpleName()+".getLongestQuestions.getLongestQuestions:"+builder.toString());
		return res;
	}
	
	public void removeQuestionCache(int userId, String subject, int questionId) {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		String key = getQuestioonCachesKey(userId, subject);
		redisUtil.lpop(key);
	}
	
	public void addQuestionCache(int userId, String subject, List<Integer> questions) {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		String[] members = new String[questions.size()];
		for(int i=0; i<members.length; i++) {
			members[i] = Integer.toString(questions.get(i));
		}
		String key = getQuestioonCachesKey(userId, subject);
		redisUtil.del(key);
		redisUtil.rpush(key, members);
		redisUtil.expire(key, QUESTION_TIMEOUT_TIME);
	}
	
	public void initUserData() {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		for(Entry<String, Map<Integer, Question>> subjectEntry : subjects.entrySet()) {
			Map<Integer, Question> subjectQuestions = subjectEntry.getValue();
			String subject = subjectEntry.getKey();
			for(Entry<Integer, Question> questionEntry : subjectQuestions.entrySet()) {
				int questionId = questionEntry.getKey();
				if(!redisUtil.sismember(getUserQuestionKey(1, subject), String.valueOf(questionId))) {
					redisUtil.sadd(getUserQuestionKey(1, subject), String.valueOf(questionId));
					touchQuestion(1, questionId, 0, subject);
				}
			}
		}
	}
	
	/**
	 * 	刷新问题的访问时间
	 * @param questionId
	 * @param time
	 */
	public void touchQuestion(int userId, int questionId, long time, String subject) {
		AbstractRedisUtil redisUtil = AbstractRedisUtil.getInstance();
		redisUtil.zadd(getQuestionsKey(userId, subject), time, String.valueOf(questionId));
	}
	
	public void loadData() {
		Map<String, List<Question>> subjects = new FileDataLoader().loadData();
		for(String subjectName : subjects.keySet()) {
			Map<Integer, Question> questions = new HashMap<>();
			for(Question q : subjects.get(subjectName)) {
				questions.put(q.getId(), q);
			}
			this.subjects.put(subjectName, questions);
		}
	}
}
