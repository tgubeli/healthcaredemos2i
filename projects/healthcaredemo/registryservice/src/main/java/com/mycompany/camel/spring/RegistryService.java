package com.mycompany.camel.spring;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.segment.AL1;
import ca.uhn.hl7v2.model.v26.segment.EVN;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.parser.Parser;

import java.io.IOException;
import java.util.Date;

public class RegistryService {

    public String registryPatient(Patient patient) {
        return createHL7NotificationString(patient);
    }

    private String createHL7NotificationString(Patient patient) {

        String result = null;
        HapiContext context = new DefaultHapiContext();
        Parser parser = context.getPipeParser();
        String encodedMessage = null;
        try {
            ADT_A01 adt = new ADT_A01();
            adt.initQuickstart("ADT", "A01", "T");

            MSH msh = adt.getMSH();
            encodedMessage = parser.encode(adt);
            System.out.println(encodedMessage);


            EVN evn = adt.getEVN();
            evn.getEvn2_RecordedDateTime().setValue(new Date());
            evn.getEvn6_EventOccurred().setValue(new Date());


            encodedMessage = parser.encode(adt);
            System.out.println(encodedMessage);

            PID pid = adt.getPID();

            pid.getPatientIdentifierList(0).getIDNumber().setValue("56782445");
            pid.getPatientIdentifierList(0).getAssigningAuthority().getHd1_NamespaceID().setValue("UAReg");
            pid.getPatientIdentifierList(0).getIdentifierTypeCode().setValue("PI");

            pid.getPatientName(0).getFamilyName().getSurname().setValue(patient.getFamilyName());
            pid.getPatientName(0).getGivenName().setValue(patient.getName());
            pid.getPatientName(0).getSecondAndFurtherGivenNamesOrInitialsThereof().setValue("Q");
            pid.getPatientName(0).getSuffixEgJRorIII().setValue("JR");

            pid.getDateTimeOfBirth().setValue(patient.getBirthday());
            pid.getAdministrativeSex().setValue(patient.getGender());

            pid.getRace(0).getIdentifier().setValue("2028-9");
            pid.getRace(0).getNameOfCodingSystem().setValue("HL70005");
            pid.getRace(0).getAlternateIdentifier().setValue(patient.getHisId());
            pid.getRace(0).getNameOfAlternateCodingSystem().setValue("XYZ");

            String[] addr = processAddress(patient.getAddress());

            pid.getPatientAddress(0).getStreetAddress().getSad1_StreetOrMailingAddress().setValue(addr[0]);
            pid.getPatientAddress(0).getOtherDesignation().setValue(addr[1]);
            pid.getPatientAddress(0).getCity().setValue(addr[2]);
            pid.getPatientAddress(0).getStateOrProvince().setValue(addr[3]);
            if (addr.length>4) {
                pid.getPatientAddress(0).getZipOrPostalCode().setValue(addr[4]);
            }

            pid.getPatientAddress(0).getAddressType().setValue("M");

            addr = processAddress(patient.getEmergencycontact());
            pid.getPatientAddress(1).getStreetAddress().getSad1_StreetOrMailingAddress().setValue(addr[0]);
            pid.getPatientAddress(1).getOtherDesignation().setValue(addr[1]);
            pid.getPatientAddress(1).getCity().setValue(addr[2]);
            pid.getPatientAddress(1).getStateOrProvince().setValue(addr[3]);
            if (addr.length>4) {
                pid.getPatientAddress(1).getZipOrPostalCode().setValue(addr[4]);
            }


            addr = processAddress(patient.getEmergencycontactAddress());

            pid.getPatientAddress(1).getCountry().setValue(addr[0]);
            pid.getPatientAddress(1).getAddressType().setValue(addr[1]);
            pid.getPatientAddress(1).getOtherGeographicDesignation().setValue(addr[2]);
            pid.getPatientAddress(1).getCountyParishCode().setValue(addr[3]);
            if (addr.length>4) {
                pid.getPatientAddress(1).getCensusTract().setValue(addr[4]);
            }


            pid.getPatientAddress(1).getAddressValidityRange().getDr1_RangeStartDateTime().setValue(new Date());

            pid.getPatientAccountNumber().getIDNumber().setValue("0105I30001");
            pid.getPatientAccountNumber().getAssigningAuthority().getHd1_NamespaceID().setValue("99DEF");
            pid.getPatientAccountNumber().getIdentifierTypeCode().setValue("AN");

            encodedMessage = parser.encode(adt);
            System.out.println(encodedMessage);

            AL1 al1 = adt.getAL1();
            al1.getAl13_AllergenCodeMnemonicDescription().getIdentifier().setValue("99999998");
            al1.getAl13_AllergenCodeMnemonicDescription().getText().setValue("No Known Drug Allergies");

            result = parser.encode(adt);

            encodedMessage = parser.encode(adt);
            System.out.println(encodedMessage);
        } catch (HL7Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        StringBuffer sb = new StringBuffer();
        sb.append("MSH|^~\\&|||||20160314112147.232+0800||ADT^A01^ADT_A01|801|T|2.6" + "\r");
        sb.append("EVN||" + RegistryUtil.genEventId() + "||||" + RegistryUtil.genEventId() + "\r");
        sb.append("PID|||56782445^^^UAReg^PI||" + patient.getFamilyName() + "^" + patient.getName() + "^Q^JR||" + patient.getBirthday() + "|"
                + patient.getGender() + "||2028-9^^HL70005^"
                + patient.getHisId() + "^^XYZ|" + RegistryUtil.genAddress() + "^^M~"
                + patient.getEmergencycontact() + "^" + patient.getEmergencycontactAddress() + "^^O|||||||0105I30001^^^99DEF^AN" + "\r");
        sb.append("AL1|||99999998^No Known Drug Allergies");
        */

        //return sb.toString();


        return result;
    }

    private String[] processAddress(String addr) {
        return addr.split("\\^");
    }

    public static void main(String... args) {
        Patient patient = new Patient();
        patient.setHisId("HIS_1");
        patient.setFamilyName("Yan");
        patient.setName("Sam");
        patient.setGender(RegistryUtil.genRandomGender());
        patient.setAddress(RegistryUtil.genAddress());
        patient.setBirthday(RegistryUtil.genRandomBirthday());
        patient.setEmergencycontact(RegistryUtil.genAddress());
        patient.setEmergencycontactAddress(RegistryUtil.genAddress());



        String s = new RegistryService().createHL7NotificationString(patient);
//        System.out.println(s);

//        String[] xx = new RegistryService().processAddress(RegistryUtil.addresses.get(1));
//        System.out.println(xx);
    }


}
