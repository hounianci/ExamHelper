package com.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.entity.Question;

public class FileDataLoader implements DataLoader {

	public static int id = 100;
	
	public Map<String, List<Question>> loadData() {
//		URL url = FileDataLoader.class.getClassLoader().getResource("questions");
//		File file = new File(url.getFile());
		File file = new File("questions");
		Map<String, List<Question>> subjects = new HashMap<String, List<Question>>();
		for(File folder : file.listFiles()) {
			String subject = folder.getName();
			List<Question> questions = new ArrayList<Question>();
			subjects.put(subject, questions);
			if(folder.listFiles()==null) {
				continue;
			}
			for(File questionFile : folder.listFiles()) {
				StringBuilder builder = new StringBuilder();
				char[] tmpChars = new char[256];
				int len = 0;
				try(Reader reader = new InputStreamReader(new FileInputStream(questionFile));){
					while ((len = reader.read(tmpChars)) != -1) {
					    builder.append(Arrays.copyOf(tmpChars, len));
					}
				}catch (Exception e) {
				}
				String fileName = questionFile.getName();
				String[] subQuestions = builder.toString().split("\n");
				int line = 0;
				while(line<subQuestions.length) {
					String subQues = subQuestions[line++];
					StringBuilder subAns = new StringBuilder();
					while(line<subQuestions.length&&!subQuestions[line].trim().equals("")) {
						subAns.append(subQuestions[line++]);
					}
					Question question = new Question();
					question.setId(id++);
					question.setQuestion(subQues);
					question.setAnswer(subAns.toString());
					questions.add(question);
					while(line<subQuestions.length&&subQuestions[line].trim().equals("")) {
						line++;
					}
				}
			}
			System.out.println(subject+" load "+questions.size()+" questions.");
		}
		return subjects;
	}
	
}
