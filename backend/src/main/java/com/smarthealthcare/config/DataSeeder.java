package com.smarthealthcare.config;

import com.smarthealthcare.entity.*;
import com.smarthealthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Seeds the symptom/disease knowledge base used by DiseasePredictionService on first boot.
 * Idempotent: skips entirely if diseases already exist, so it's safe to leave enabled.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SymptomRepository symptomRepository;
    private final DiseaseRepository diseaseRepository;
    private final DiseaseSymptomWeightRepository weightRepository;

    private final Map<String, Symptom> symptomCache = new HashMap<>();

    @Override
    @Transactional
    public void run(String... args) {
        if (diseaseRepository.count() > 0) return;

        seedSymptoms();
        seedDiseases();
    }

    private void seedSymptoms() {
        String[][] symptoms = {
            // name, displayName, category
            {"fever", "Fever", "General"},
            {"high_fever", "High Fever (>102°F)", "General"},
            {"chills", "Chills", "General"},
            {"fatigue", "Fatigue / Tiredness", "General"},
            {"body_ache", "Body Ache", "General"},
            {"headache", "Headache", "Neurological"},
            {"severe_headache", "Severe Headache", "Neurological"},
            {"dizziness", "Dizziness", "Neurological"},
            {"confusion", "Confusion", "Neurological"},
            {"loss_of_consciousness", "Loss of Consciousness", "Neurological"},
            {"dry_cough", "Dry Cough", "Respiratory"},
            {"wet_cough", "Cough with Mucus", "Respiratory"},
            {"sore_throat", "Sore Throat", "Respiratory"},
            {"runny_nose", "Runny Nose", "Respiratory"},
            {"nasal_congestion", "Nasal Congestion", "Respiratory"},
            {"shortness_of_breath", "Shortness of Breath", "Respiratory"},
            {"wheezing", "Wheezing", "Respiratory"},
            {"chest_pain", "Chest Pain / Pressure", "Cardiac"},
            {"palpitations", "Heart Palpitations", "Cardiac"},
            {"pain_radiating_to_arm", "Pain Radiating to Arm/Jaw", "Cardiac"},
            {"sweating", "Excessive Sweating", "Cardiac"},
            {"nausea", "Nausea", "Gastrointestinal"},
            {"vomiting", "Vomiting", "Gastrointestinal"},
            {"diarrhea", "Diarrhea", "Gastrointestinal"},
            {"abdominal_pain", "Abdominal Pain", "Gastrointestinal"},
            {"loss_of_appetite", "Loss of Appetite", "Gastrointestinal"},
            {"bloating", "Bloating", "Gastrointestinal"},
            {"heartburn", "Heartburn", "Gastrointestinal"},
            {"joint_pain", "Joint Pain", "Musculoskeletal"},
            {"muscle_pain", "Muscle Pain", "Musculoskeletal"},
            {"back_pain", "Back Pain", "Musculoskeletal"},
            {"swelling", "Swelling", "Musculoskeletal"},
            {"rash", "Skin Rash", "Dermatological"},
            {"itching", "Itching", "Dermatological"},
            {"red_eyes", "Red / Watery Eyes", "ENT"},
            {"ear_pain", "Ear Pain", "ENT"},
            {"frequent_urination", "Frequent Urination", "Urological"},
            {"excessive_thirst", "Excessive Thirst", "Endocrine"},
            {"unexplained_weight_loss", "Unexplained Weight Loss", "Endocrine"},
            {"blurred_vision", "Blurred Vision", "Endocrine"},
            {"anxiety", "Anxiety", "Mental Health"},
            {"insomnia", "Insomnia / Trouble Sleeping", "Mental Health"},
            {"low_mood", "Persistent Low Mood", "Mental Health"},
            {"stiff_neck", "Stiff Neck", "Neurological"},
            {"sensitivity_to_light", "Sensitivity to Light", "Neurological"},
            {"burning_urination", "Burning Sensation While Urinating", "Urological"},
        };

        for (String[] s : symptoms) {
            Symptom symptom = new Symptom(null, s[0], s[1], s[2]);
            symptom = symptomRepository.save(symptom);
            symptomCache.put(s[0], symptom);
        }
    }

    private void seedDiseases() {
        seedDisease("Common Cold", "General Physician", Disease.Severity.LOW,
                "A mild viral infection of the nose and throat. Usually resolves on its own within a week.",
                w("runny_nose", 8, false), w("sore_throat", 7, false), w("nasal_congestion", 8, false),
                w("dry_cough", 5, false), w("fever", 3, false), w("headache", 3, false));

        seedDisease("Influenza (Flu)", "General Physician", Disease.Severity.MODERATE,
                "A viral respiratory infection, more severe than a common cold, with sudden onset.",
                w("high_fever", 9, true), w("chills", 7, false), w("body_ache", 8, false),
                w("fatigue", 7, false), w("dry_cough", 6, false), w("headache", 5, false), w("sore_throat", 4, false));

        seedDisease("COVID-19", "General Physician", Disease.Severity.MODERATE,
                "A viral respiratory illness which can range from mild to severe, sometimes affecting smell/taste.",
                w("fever", 7, false), w("dry_cough", 7, false), w("fatigue", 6, false),
                w("shortness_of_breath", 8, false), w("sore_throat", 4, false), w("body_ache", 5, false),
                w("headache", 4, false));

        seedDisease("Bronchitis", "Pulmonologist", Disease.Severity.MODERATE,
                "Inflammation of the airways, often following a cold, causing a persistent productive cough.",
                w("wet_cough", 9, true), w("chest_pain", 4, false), w("fatigue", 5, false),
                w("shortness_of_breath", 5, false), w("wheezing", 6, false), w("fever", 3, false));

        seedDisease("Asthma Exacerbation", "Pulmonologist", Disease.Severity.HIGH,
                "A flare-up of airway inflammation causing breathing difficulty; can become an emergency.",
                w("wheezing", 9, true), w("shortness_of_breath", 9, true), w("chest_pain", 5, false),
                w("dry_cough", 5, false), w("anxiety", 3, false));

        seedDisease("Pneumonia", "Pulmonologist", Disease.Severity.HIGH,
                "A lung infection that inflames air sacs, potentially serious especially in vulnerable patients.",
                w("high_fever", 8, false), w("wet_cough", 8, true), w("shortness_of_breath", 8, false),
                w("chest_pain", 7, false), w("fatigue", 6, false), w("chills", 5, false));

        seedDisease("Acute Myocardial Infarction (Heart Attack)", "Cardiologist", Disease.Severity.EMERGENCY,
                "A medical emergency where blood flow to the heart is blocked. Seek immediate emergency care.",
                w("chest_pain", 10, true), w("pain_radiating_to_arm", 9, false), w("sweating", 7, false),
                w("shortness_of_breath", 7, false), w("nausea", 5, false), w("dizziness", 5, false));

        seedDisease("Arrhythmia", "Cardiologist", Disease.Severity.HIGH,
                "An irregular heartbeat that can range from harmless to life-threatening.",
                w("palpitations", 9, true), w("dizziness", 6, false), w("chest_pain", 5, false),
                w("shortness_of_breath", 5, false), w("fatigue", 4, false));

        seedDisease("Hypertension-related Episode", "Cardiologist", Disease.Severity.HIGH,
                "Symptoms consistent with dangerously elevated blood pressure.",
                w("severe_headache", 7, false), w("dizziness", 6, false), w("blurred_vision", 6, false),
                w("chest_pain", 5, false), w("nausea", 3, false));

        seedDisease("Gastroenteritis", "Gastroenterologist", Disease.Severity.MODERATE,
                "Inflammation of the stomach/intestines, usually from infection, causing GI symptoms.",
                w("diarrhea", 9, true), w("vomiting", 7, false), w("abdominal_pain", 7, false),
                w("nausea", 6, false), w("fever", 4, false), w("fatigue", 3, false));

        seedDisease("Gastroesophageal Reflux Disease (GERD)", "Gastroenterologist", Disease.Severity.LOW,
                "Chronic acid reflux causing heartburn and regurgitation.",
                w("heartburn", 9, true), w("bloating", 5, false), w("nausea", 3, false),
                w("chest_pain", 4, false), w("sore_throat", 2, false));

        seedDisease("Peptic Ulcer Disease", "Gastroenterologist", Disease.Severity.MODERATE,
                "Sores on the stomach lining causing pain, often worse on an empty stomach.",
                w("abdominal_pain", 9, true), w("bloating", 5, false), w("loss_of_appetite", 5, false),
                w("nausea", 5, false), w("heartburn", 4, false));

        seedDisease("Migraine", "Neurologist", Disease.Severity.MODERATE,
                "A neurological condition causing intense, often one-sided headaches with additional symptoms.",
                w("severe_headache", 9, true), w("sensitivity_to_light", 7, false), w("nausea", 6, false),
                w("dizziness", 4, false), w("vomiting", 3, false));

        seedDisease("Meningitis", "Neurologist", Disease.Severity.EMERGENCY,
                "A serious infection/inflammation of the membranes around the brain and spinal cord.",
                w("stiff_neck", 8, true), w("high_fever", 8, false), w("severe_headache", 8, false),
                w("sensitivity_to_light", 6, false), w("confusion", 6, false), w("vomiting", 4, false));

        seedDisease("Tension Headache", "General Physician", Disease.Severity.LOW,
                "A common headache linked to stress or muscle tension, usually mild-to-moderate.",
                w("headache", 8, true), w("fatigue", 4, false), w("insomnia", 3, false), w("anxiety", 3, false));

        seedDisease("Type 2 Diabetes (Uncontrolled)", "Endocrinologist", Disease.Severity.HIGH,
                "Symptoms suggesting elevated blood sugar that needs prompt evaluation and management.",
                w("excessive_thirst", 8, true), w("frequent_urination", 8, true), w("fatigue", 6, false),
                w("blurred_vision", 6, false), w("unexplained_weight_loss", 5, false));

        seedDisease("Urinary Tract Infection (UTI)", "General Physician", Disease.Severity.MODERATE,
                "A bacterial infection of the urinary tract, common and very treatable.",
                w("burning_urination", 9, true), w("frequent_urination", 7, false), w("abdominal_pain", 4, false),
                w("fever", 3, false));

        seedDisease("Allergic Rhinitis", "General Physician", Disease.Severity.LOW,
                "An allergic reaction causing nasal and eye symptoms, often seasonal.",
                w("runny_nose", 7, false), w("nasal_congestion", 7, false), w("red_eyes", 7, false),
                w("itching", 6, false), w("sore_throat", 2, false));

        seedDisease("Contact Dermatitis / Skin Allergy", "Dermatologist", Disease.Severity.LOW,
                "A skin reaction to an irritant or allergen causing rash and itching.",
                w("rash", 9, true), w("itching", 8, false), w("swelling", 4, false));

        seedDisease("Rheumatoid / Joint Inflammation", "Orthopedist", Disease.Severity.MODERATE,
                "Joint inflammation causing pain, stiffness and swelling, potentially chronic.",
                w("joint_pain", 9, true), w("swelling", 6, false), w("fatigue", 4, false), w("muscle_pain", 4, false));

        seedDisease("Generalized Anxiety / Stress Response", "Psychiatrist", Disease.Severity.MODERATE,
                "Symptoms consistent with significant stress or an anxiety disorder affecting daily life.",
                w("anxiety", 8, true), w("insomnia", 6, false), w("palpitations", 4, false),
                w("fatigue", 4, false), w("low_mood", 3, false));

        seedDisease("Depressive Episode", "Psychiatrist", Disease.Severity.MODERATE,
                "A period of persistent low mood and loss of interest that may benefit from professional support.",
                w("low_mood", 8, true), w("insomnia", 5, false), w("fatigue", 6, false),
                w("loss_of_appetite", 4, false), w("anxiety", 3, false));

        seedDisease("Ear Infection (Otitis)", "ENT Specialist", Disease.Severity.LOW,
                "An infection of the ear canal or middle ear, common in both children and adults.",
                w("ear_pain", 9, true), w("fever", 4, false), w("dizziness", 3, false));

        seedDisease("Sinusitis", "ENT Specialist", Disease.Severity.LOW,
                "Inflammation of the sinuses, often following a cold, causing facial pressure and congestion.",
                w("nasal_congestion", 8, false), w("headache", 5, false), w("sore_throat", 3, false),
                w("fever", 3, false), w("fatigue", 3, false));
    }

    private void seedDisease(String name, String specialty, Disease.Severity severity, String description, SW... weights) {
        Disease disease = new Disease();
        disease.setName(name);
        disease.setRecommendedSpecialty(specialty);
        disease.setBaseSeverity(severity);
        disease.setDescription(description);
        disease = diseaseRepository.save(disease);

        for (SW sw : weights) {
            Symptom symptom = symptomCache.get(sw.symptomName);
            if (symptom == null) continue;
            DiseaseSymptomWeight edge = new DiseaseSymptomWeight();
            edge.setDisease(disease);
            edge.setSymptom(symptom);
            edge.setWeight(sw.weight);
            edge.setCoreSymptom(sw.core);
            weightRepository.save(edge);
        }
    }

    private SW w(String symptomName, int weight, boolean core) {
        return new SW(symptomName, weight, core);
    }

    private record SW(String symptomName, int weight, boolean core) {}
}
