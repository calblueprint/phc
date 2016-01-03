require 'rails_helper'

describe EventRegistration, type: :model do
  describe "#to_salesforce_object" do
    let(:account) { Account.create(sf_id: 123) }
    let(:event_registration) do
      EventRegistration.create({
          account_id: account.id,
          Feedback__c: "great",
          Experience__c: 5,
          Services_Needed__c: ["kick boxing", "taichi"]
        })
    end
    let(:obj) { event_registration.to_salesforce_object }

    it "includes the account salesforce id" do
      expect(obj["Account__c"]).to eq(account.sf_id)
    end

    it "includes PHC_Event__c" do
      allow(ENV).to receive(:[]).and_return("123xyz")
      expect(obj["PHC_Event__c"]).to eq("123xyz")
    end

    it "includes unserialized services needed" do
      expect(obj["Services_Needed__c"]).to eq("kick boxing, taichi")
    end

    it "populates services and their statuses" do
      service1 = event_registration.services.create(name:"Footwash")
      service2 = event_registration.services.create(name:"Cake baking")
      service1.applied!
      service2.drop_in!
      expect(obj[service1.name]).to eq("Applied")
      expect(obj[service2.name]).to eq("Drop In")
    end
  end

end
