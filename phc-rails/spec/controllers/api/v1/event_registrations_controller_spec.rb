require 'rails_helper'

describe Api::V1::EventRegistrationsController, type: :controller do
  before do
    allow(subject).to receive(:verify_security_token).and_return(true)
  end

  describe "#create" do
    let(:services) do
      {
        footwash: true,
        haircut: true,
        food: false
      }
    end

    let(:account_params) do
      {
        FirstName: "testfirst",
        LastName: "testlast",
      }
    end

    let(:params) do
      {
        account_sfid: "abc123",
        Number__c: 1234
      }.merge!(services).
        merge!(account_params)
    end

    let(:account) { Account.create(sf_id: "abc123") }

    before :all do
      ServiceList.delete_all
      ServiceList.create(salesforce_name: "footwash")
      ServiceList.create(salesforce_name: "haircut")
      ServiceList.create(salesforce_name: "food")
    end

    before do
      Account.delete_all
      EventRegistration.delete_all
    end

    context "when the account does not exist" do
      it "creates an account if it can't be found" do
        params.delete(:account_sfid)
        post :create, params
        expect(Account.count).to eq(1)
        expect(Account.first.FirstName).to eq("testfirst")
        expect(Account.first.LastName).to eq("testlast")
      end

      it "creates an account if salesforce id is blank" do
        params[:account_sfid] = ""
        post :create, params
        expect(Account.count).to eq(1)
      end
    end

    context "when account exists" do
      let!(:account) { Account.create(sf_id: "abc123") }

      it "updates only permitted account attributes" do
        params[:unpermitted] = "hello"
        post :create, params

        account.reload
        expect(account.FirstName).to eq("testfirst")
        expect(account.LastName).to eq("testlast")
        expect(account.updated).to eq(false)
      end

      it "creates an event registration for the account" do
        post :create, params
        expect(account.event_registrations.count).to eq(1)
      end

      it "adds only the services that were applied to the event registration" do
        post :create, params
        event_reg = account.event_registrations.first
        expect(event_reg.services.map(&:name)).
          to contain_exactly("footwash", "haircut")
        expect(event_reg.services[0].applied?).to eq(true)
        expect(event_reg.services[1].applied?).to eq(true)
      end

      it "returns success if event registration was saved" do
        post :create, params
        expect(response.status).to eq(200)
        expect(JSON.parse(response.body)).to include("status" => true, "message" => anything)
      end
    end
  end

  describe "#search" do
    before do
      Account.delete_all
      EventRegistration.delete_all
    end

    it "returns true if the event registration exists" do
      EventRegistration.create(Number__c: 1234)
      request.env["HTTP_NUMBER__C"] = 1234
      get :search
      expect(JSON.parse(response.body)).to include("present" => true)
    end

    it "returns false if event registration does not exist" do
      request.env["HTTP_NUMBER__C"] = 0000
      get :search
      expect(JSON.parse(response.body)).to include("present" => false)
    end
  end

  describe "#get_applied" do
    before do
      EventRegistration.delete_all
    end

    it "returns the services applied for" do
      event_registration = EventRegistration.create(Number__c: 1234)
      event_registration.services.create(name: "footwash").applied!
      event_registration.services.create(name: "food").applied!
      event_registration.services.create(name: "insurance").unspecified!
      get :get_applied, Number__c: 1234
      expect(JSON.parse(response.body)).to include("services" => ["footwash", "food"])
    end

    it "returns empty array if no services were applied for" do
      EventRegistration.create(Number__c: 1234)
      get :get_applied, Number__c: 1234
      expect(JSON.parse(response.body)).to include("services" => [])
    end

    it "returns 404 if event registration does not exist" do
      get :get_applied, Number__c: 1234
      expect(response.status).to eq(404)
    end
  end

  describe "#update_service" do
    before do
      EventRegistration.delete_all
      Service.delete_all
    end

    it "returns 404 if event registration does not exist" do
      get :update_service, Number__c: 1234
      expect(response.status).to eq(404)
    end

    it "returns 404 if service name does not exist" do
      EventRegistration.create(Number__c: 1234)
      get :update_service, Number__c: 1234, service_name: "hotdogs"
      expect(response.status).to eq(404)
    end

    context "with valid event registration and service" do
      before do
        Service.delete_all
      end

      let!(:event_registration) { EventRegistration.create(Number__c: 1234) }
      let!(:service) { event_registration.services.create(name: "footwash") }

      it "sets unspecified status to drop in" do
        service.unspecified!
        get :update_service, Number__c: 1234, service_name: "footwash"
        expect(service.reload.status).to eq("drop_in")
      end

      it "sets applied status to received" do
        service.applied!
        get :update_service, Number__c: 1234, service_name: "footwash"
        expect(service.reload.status).to eq("received")
      end

      it "does not change drop in status" do
        service.drop_in!
        get :update_service, Number__c: 1234, service_name: "footwash"
        expect(service.reload.status).to eq("drop_in")
      end

      it "does not change received status" do
        service.received!
        get :update_service, Number__c: 1234, service_name: "footwash"
        expect(service.reload.status).to eq("received")
      end
    end
  end

  describe "#update_feedback" do
    it "updates the feedback" do
      event_registration = EventRegistration.create(Number__c: 1234)
      post :update_feedback,
        Number__c: 1234,
        Experience__c: 5,
        Feedback__c: "needs more cowbell",
        Services_Needed__c: ["mud wrestling", "sky diving", "interpretive dance"],
        phc_sfid: "should not be updated"
      expect(event_registration.reload.Services_Needed__c).to eq(["mud wrestling", "sky diving", "interpretive dance"])
      expect(event_registration.reload.Experience__c).to eq(5)
      expect(event_registration.reload.Feedback__c).to eq("needs more cowbell")
      expect(event_registration.reload.phc_sfid).to be_nil
    end

    it "returns 400 if event registration can't be found" do
      post :update_feedback, Number__c: 1234
      expect(response.status).to eq(400)
    end
  end
end
