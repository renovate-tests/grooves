package grooves.example.grails.mongo

import com.github.rahulsom.grooves.api.EventsDsl
import grooves.grails.mongo.Patient
import grooves.grails.mongo.PatientAccountQuery
import grooves.grails.mongo.PatientCreated
import grooves.grails.mongo.PatientEvent
import grooves.grails.mongo.PatientHealthQuery
import grooves.grails.mongo.PaymentMade
import grooves.grails.mongo.ProcedurePerformed

import java.text.SimpleDateFormat
import java.util.function.Consumer

class BootStrap {

    def init = { servletContext ->
        def patient = new Patient(uniqueId: '42').save(flush: true, failOnError: true)

        on(patient) {
            apply new PatientCreated(name: 'John Lennon')
            apply new ProcedurePerformed(code: 'FLUSHOT', cost: 32.40)
            apply new ProcedurePerformed(code: 'GLUCOSETEST', cost: 78.93)
            apply new PaymentMade(amount: 100.25)

            snapshotWith new PatientAccountQuery()
            snapshotWith new PatientHealthQuery()

            apply new ProcedurePerformed(code: 'ANNUALPHYSICAL', cost: 170.00)
            apply new PaymentMade(amount: 180.00)

            snapshotWith new PatientAccountQuery()
            snapshotWith new PatientHealthQuery()
        }

        def patient2 = new Patient(uniqueId: '43').save(flush: true, failOnError: true)

        on(patient2) {
            apply new PatientCreated(name: 'Ringo Starr')
            apply new ProcedurePerformed(code: 'ANNUALPHYSICAL', cost: 170.00)
            apply new ProcedurePerformed(code: 'GLUCOSETEST', cost: 78.93)
            apply new PaymentMade(amount: 100.25)

            snapshotWith new PatientAccountQuery()
            snapshotWith new PatientHealthQuery()

            apply new ProcedurePerformed(code: 'FLUSHOT', cost: 32.40)
            apply new PaymentMade(amount: 180.00)

            snapshotWith new PatientAccountQuery()
            snapshotWith new PatientHealthQuery()
        }
    }

    Date currDate = Date.parse('yyyy-MM-dd', '2016-01-01')

    void on(Patient patient, @DelegatesTo(EventsDsl.OnSpec) Closure closure) {
        def eventSaver = { it.save(flush: true, failOnError: true) } as Consumer
        def positionSupplier = { PatientEvent.countByAggregate(patient) + 1 }
        def userSupplier = {'anonymous'}
        def dateSupplier = {currDate+=1; currDate}
        EventsDsl.on(patient, eventSaver, positionSupplier, userSupplier, dateSupplier, closure)
    }

    def destroy = {
    }
}