require 'rails_helper'

describe Api::V1::AccountsController do
  before :each do
    controller.stub(:verify_security_token => true)
  end

  describe "api/v1/" do


    describe "GET #search" do
      it "returns a list of fuzzy matches of a name" do
        account = FactoryGirl.create(:account, FirstName: "Bob")
        account = FactoryGirl.create_list(:account, 21, FirstName: "Bobr")
        get "search", FirstName: "Bob"
        expect(response).to be_success
        json = JSON.parse(response.body)
        expect(json.length).to eq(22)
      end

      it "paginates the results if cursor is specified" do
        account = FactoryGirl.create(:account, FirstName: "Bob")
        account = FactoryGirl.create_list(:account, 21, FirstName: "Bobr")
        get "search", FirstName: "Bob", cursor: 1
        expect(response).to be_success
        json = JSON.parse(response.body)
        expect(json.length).to eq(20)
        expect(json[0]['FirstName']).to eq('Bobr')
      end
    end

    describe "GET #check" do
    end

    describe "GET #accounts/:sf_id" do
      it "returns a JSON of the account if it exists" do
        account = FactoryGirl.create(:account)
        get "show", sf_id: account.sf_id
        expect(response).to be_success
      end

      it "returns an error if the account does not exist" do
        get "show", sf_id: 12345
        expect(response).to have_http_status(:not_found)
      end
    end

    describe "POST #create" do
    end

  end

end