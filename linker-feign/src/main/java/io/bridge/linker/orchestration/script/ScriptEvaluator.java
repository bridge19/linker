/**
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package io.bridge.linker.orchestration.script;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author yaochen
 *
 */
public class ScriptEvaluator {

	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	
	private ScriptEvaluator(){
		
	}
	
	public static Object eval(String script, Object... inputs) throws ScriptException {
		Bindings bindings = engine.createBindings();
		for(int i=1;i<=inputs.length;i++) {
			bindings.put("$" + i, inputs[i - 1]);
		}
		String scriptExpressionBuilder = "function scriptFunc(){" +
				script +
				"} scriptFunc();";
		return engine.eval(scriptExpressionBuilder, bindings);
	}
}
