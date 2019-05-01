package ideals.inner

import com.fasterxml.jackson.annotation.JsonProperty
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class SecondInner(

	@field:JsonProperty("object_field")
	val objectField: Any? = null,

	@field:JsonProperty("int_field")
	val intField: Int? = null,

	@field:JsonProperty("double_field")
	val doubleField: Double? = null,

	@field:JsonProperty("string_field")
	val stringField: String? = null
)