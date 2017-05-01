package edu.gvsu.prestongarno;

import com.sun.source.util.*;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Options;

import static com.sun.tools.javac.main.Option.*;

/*****************************************
 * Created by preston on 4/26/17.
 *
 * Plugin for the ServiceLoader
 ****************************************/
public class Loader implements Plugin {

	@Override
	public String getName() {
		return "TryWithRes-<<Port";
	}

	/*****************************************
	 * @param javacTask the comp. task
	 * @param strings args
	 ****************************************/
	@Override
	public void init(JavacTask javacTask, String... strings) {
			javacTask.addTaskListener(createTaskListener(javacTask));
	}

	/*****************************************
	 * @param task the compilation task
	 * @return TaskListener that transforms try-with-res to
	 * a safe but backwards compatible version
	 ****************************************/
	public static TaskListener createTaskListener(JavacTask task) {
		return new TaskListenerImpl(task);
	}


	/*****************************************
	 * The tasklistener waits until the
	 * processing rounds are over to scan the AST
	 ****************************************/
	static final class TaskListenerImpl implements TaskListener {
		boolean transformed;
		final boolean PRINT;
		private BasicJavacTask task;

		public TaskListenerImpl(JavacTask javacTask)
		{
			task = (BasicJavacTask) javacTask;
			Options options = Options.instance(task.getContext());
			transformed = false;
			PRINT = options.isSet(VERBOSE);
		}

		public void started(TaskEvent taskEvent) {}

		public void finished(TaskEvent taskEvent) {
			if(!transformed && taskEvent.getKind() == TaskEvent.Kind.ANALYZE) {
				TryTreeTranslator ttt = new TryTreeTranslator(task.getContext());
				if(PRINT) {
					System.out.println("\n============<BEFORE>============");
					taskEvent.getCompilationUnit().getTypeDecls().forEach(System.out::println);
				}

				taskEvent.getCompilationUnit().getTypeDecls().forEach(o -> ttt.translateClass(((JCTree.JCClassDecl) o)));

				if(PRINT) {
					System.out.println("\n============<AFTER>============");
					taskEvent.getCompilationUnit().getTypeDecls().forEach(System.out::println);
				}
				transformed = true;
			}
		}
	}
}
