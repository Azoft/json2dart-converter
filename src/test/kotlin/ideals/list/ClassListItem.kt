package ideals.list

import com.fasterxml.jackson.annotation.JsonProperty
import javax.annotation.Generated


data class ClassListItem(

	@field:JsonProperty("object_field")
	val objectField: Any? = null,

	@field:JsonProperty("int_field")
	val intField: Int? = null,

	@field:JsonProperty("double_field")
	val doubleField: Double? = null,

	@field:JsonProperty("string_field")
	val stringField: String? = null
)