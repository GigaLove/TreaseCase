#include <string.h>
#include <jni.h>

jdouble
Java_hit_treasure_activity_CalculatorActivity_add( JNIEnv* env,
        jobject thiz, jdouble num1, jdouble num2 )
{
	return (num1 + num2);
}

jdouble
Java_hit_treasure_activity_CalculatorActivity_sub( JNIEnv* env,
        jobject thiz, jdouble num1, jdouble num2 )
{
	return (num1 - num2);
}

jdouble
Java_hit_treasure_activity_CalculatorActivity_mul( JNIEnv* env,
        jobject thiz, jdouble num1, jdouble num2 )
{
	return (num1 * num2);
}

jdouble
Java_hit_treasure_activity_CalculatorActivity_div( JNIEnv* env,
        jobject thiz, jdouble num1, jdouble num2 )
{
	return (num1 / num2);
}


