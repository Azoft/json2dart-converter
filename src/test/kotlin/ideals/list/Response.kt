package ideals.list

import com.fasterxml.jackson.annotation.JsonProperty
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Response(

	@field:JsonProperty("double_list")
	val doubleList: List<Double?>? = null,

	@field:JsonProperty("object_list")
	val objectList: List<Any?>? = null,

	@field:JsonProperty("int_list")
	val intList: List<Int?>? = null,

	@field:JsonProperty("string_list")
	val stringList: List<String?>? = null,

	@field:JsonProperty("class_list")
	val classList: List<ClassListItem?>? = null
)