package grooves.grails.mongo

import com.github.rahulsom.grooves.annotations.Aggregate
import com.github.rahulsom.grooves.api.AggregateType

@Aggregate
class Patient implements AggregateType<Long> {

    String uniqueId
    static constraints = {
    }
}
