package hit.treasure.activity;

import java.util.Stack;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalculatorActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);
		
		// 初始化相应stack和stringBuilder
		numStack = new Stack<Double>();
		operateStack = new Stack<String>();		
		lastNumBuilder = new StringBuilder();
		commandBuilder = new StringBuilder();
		
		initView();		
	}
	
	/**
	 * 初始化按键等按钮，以及添加按钮监听事件
	 */
	private void initView() {
		inputEditText = (EditText)findViewById(R.id.input);
		inputEditText.setKeyListener(null);
		inputEditText.setText("0");
		
		resultEditText = (EditText)findViewById(R.id.result);
		resultEditText.setKeyListener(null);
		resultEditText.setText("0");
		
		numButton = new Button[10];
		operatorButton = new Button[4];
		numID = new int[]{R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4,
				R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9};
		operatorID = new int[]{R.id.add, R.id.sub, R.id.multiply, R.id.divide};
		
		numButton[0] = (Button)findViewById(R.id.num0);
		numButton[1] = (Button)findViewById(R.id.num1);
		numButton[2] = (Button)findViewById(R.id.num2);
		numButton[3] = (Button)findViewById(R.id.num3);
		numButton[4] = (Button)findViewById(R.id.num4);
		numButton[5] = (Button)findViewById(R.id.num5);
		numButton[6] = (Button)findViewById(R.id.num6);
		numButton[7] = (Button)findViewById(R.id.num7);
		numButton[8] = (Button)findViewById(R.id.num8);
		numButton[9] = (Button)findViewById(R.id.num9);
		
		// 添加按键监听事件
		for (Button button : numButton) {
			button.setOnClickListener(this);
		}
		
		operatorButton[0] = (Button)findViewById(R.id.add);
		operatorButton[1] = (Button)findViewById(R.id.sub);
		operatorButton[2] = (Button)findViewById(R.id.multiply);
		operatorButton[3] = (Button)findViewById(R.id.divide);
		
		// 添加按键监听事件
		for (Button button : operatorButton) {
			button.setOnClickListener(this);
		}
		
		point = (Button)findViewById(R.id.point);
		equal = (Button)findViewById(R.id.equal);
		backspace = (Button)findViewById(R.id.backspace);
		clear = (Button)findViewById(R.id.clear);
		
		point.setOnClickListener(this);
		equal.setOnClickListener(this);
		backspace.setOnClickListener(this);
		clear.setOnClickListener(this);
		
		Toast.makeText(CalculatorActivity.this, add(10, 12) + "", Toast.LENGTH_LONG)
		.show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewID = v.getId();
		
		// 如果为数字的话，添加到当前操作数的stringBuilder中，并更新显示
		for (int i = 0; i < numID.length; i++) {
			if (viewID == numID[i]) {
				lastNumBuilder.append(i);
				commandBuilder.append(i);
				inputEditText.setText(commandBuilder.toString());
				return;
			}
		}
		
		// 如果是运算符的话，将当前操作数从stringBuilder中提取出来，
		// 添加到操作数栈中，同时将运算符同时压栈
		for (int i = 0; i < operatorID.length; i++) {
			if (viewID == operatorID[i]) {	
				if (lastNumBuilder.length() > 0) {
					try {
						Double operand = Double.parseDouble(lastNumBuilder.toString());
						numStack.push(operand);
						operateStack.push(OPERATORS[i]);
						commandBuilder.append(OPERATORS[i]);
						inputEditText.setText(commandBuilder);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						lastNumBuilder.delete(0, lastNumBuilder.length());
					}
				}				
				return;
			}
		}
		
		// 如果为"."的话，则判断当前操作数中是否有"."，无则添加
		if (viewID == R.id.point) {
			if (!lastNumBuilder.toString().contains(".")) {
				lastNumBuilder.append(".");
				commandBuilder.append(".");
				inputEditText.setText(commandBuilder);
			}
		}
		
		// 后退键响应事件
		if (viewID == R.id.backspace) {
			if (commandBuilder.length() > 0) {
				char c = commandBuilder.charAt(commandBuilder.length() - 1);
				if (isOperator(Character.toString(c))) {
					// 若为操作符的话，操作符退栈，操作数退回到stringBuilder中
					operateStack.pop();
					lastNumBuilder.append(numStack.pop());
				}
				else if ((c > '0' && c < '9') || c == '.') {
					// 若上一个字符为数字或者"."的话，仅修改builder即可
					lastNumBuilder.deleteCharAt(lastNumBuilder.length() - 1);
				} else {}
				commandBuilder.deleteCharAt(commandBuilder.length() - 1);
				inputEditText.setText(commandBuilder);
			}
		}
		
		// 清除按钮，清空相应的栈和builder
		if (viewID == R.id.clear) {
			clearOperate();
			inputEditText.setText("0");
			resultEditText.setText("0");
		}
		
		if (viewID == R.id.equal) {
			if (lastNumBuilder.length() > 0) {
				try {
					// 将最后一个操作数压入栈
					numStack.push(Double.parseDouble(lastNumBuilder.toString()));
					
					//计算结果并显示
					double result = calculate(numStack, operateStack);
					resultEditText.setText(Double.toString(result));
					clearOperate();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void clearOperate() {
		numStack.clear();
		operateStack.clear();
		lastNumBuilder.delete(0, lastNumBuilder.length());
		commandBuilder.delete(0, commandBuilder.length());
	}
	
	private double calculate(Stack<Double> nStack, Stack<String> opeStack) {
		// 所有运算符都弹出后，弹出最后一个操作数
		if (opeStack.empty() && nStack.size() == 1) {
			return nStack.pop();
		}
		double numOne = nStack.pop();
		String opeStr = opeStack.peek();
		if (opeStr.equals("*") || opeStr.equals("/")) {
			// 将连续的乘除法统一计算完
			numOne = calculator(nStack, opeStack, numOne);
		}
		if (!opeStack.empty()) {
			opeStr = opeStack.pop();
			if (opeStr.equals("+")) {
				return calculate(nStack, opeStack) + numOne;
			}
			return calculate(nStack, opeStack) - numOne;
		}
		else 
		{
			return numOne;
		}
	}
	
	// 主要用于计算乘除法，获得连续乘除法后的结果
	private double calculator(Stack<Double> nStack, Stack<String> opeStack, double num) {
		if (opeStack.empty()) {
			return num;
		}
		String opeStr = opeStack.pop();
		if (opeStr.equals("+") || opeStr.equals("-")) {
			opeStack.push(opeStr);
			return num;
		}
		double numTwo = nStack.pop();
		if (opeStr.equals("*")) {
			return calculator(nStack, opeStack, numTwo) * num;		
		}
		return calculator(nStack, opeStack, numTwo) / num;		
	}
	
	// 用于判断是否是运算符
	private boolean isOperator(String opeStr) {
		for (String str : OPERATORS) {
			if (str.equals(opeStr)) {
				return true;
			}
		}
		return false;
	}
	
	public native double add(double num1, double num2);
	public native double sub(double num1, double num2);
	public native double mul(double num1, double num2);
	public native double div(double num1, double num2);
	
	static {
        System.loadLibrary("calculator");
    }
	
	private static final String[] OPERATORS = {"+", "-", "*", "/"};
	private StringBuilder lastNumBuilder;		// 当前操作数
	private StringBuilder commandBuilder;		// 当前所有命令
	private Stack<Double> numStack;				// 操作数栈
	private Stack<String> operateStack;			// 操作符栈
	private EditText inputEditText;				// 编辑框
	private EditText resultEditText;
	private Button[] numButton;					// 数字按钮
	private Button[] operatorButton;			// 操作符按钮
	private int[] numID;						// 数字按键对应的资源ID	
	private int[] operatorID;					// 操作按键对应的资源ID
	private Button backspace;					// 后退键
	private Button clear;						// 清除按钮
	private Button point;						// 小数点按钮
	private Button equal;						// 结果按钮
}
