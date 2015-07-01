event_registration_params = [
  {
    FirstName: "Daniel",
    LastName: "Li",
    Number__c: 7,
    account_sfid: 12345,
    phc_sfid: 12345,
  },
  {
    FirstName: "Warren",
    LastName: "Shen",
    Number__c: 8,
    account_sfid: 23456,
    phc_sfid: 23456,
  },
]

event_registration_params.each do |event_registration_param|
  new_event_registration = EventRegistration.create(event_registration_param)
  puts "Created event_registration: #{new_event_registration.FirstName}."
end
