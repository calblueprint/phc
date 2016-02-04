require 'rails_helper'

describe Api::V1::AccountsController, type: :controller do
  before do
    allow(subject).to receive(:verify_security_token).and_return(true)
  end

  describe "#show" do
    it "returns the account if it exists" do
      account = Account.create(sf_id: "abc123")
      get :show, sf_id: "abc123"
      expect(JSON.parse(response.body)).to include("sf_id" => "abc123")
    end

    it "returns 400 if account does not exist" do
      Account.delete_all
      get :show, sf_id: "123"
      expect(response.status).to eq(400)
    end
  end

  describe "#search" do
    let(:params) do
      {
        FirstName: "first",
        LastName: "last",
      }
    end

    let(:account) { Account.create(params) }

    it "fuzzy searches using first and last name" do

    end

    it "returns 20 results by default" do
    end

    it "returns empty array if no matches found" do

    end
  end

  describe "#create" do
    it "creates an account" do

    end

    it "returns 400 if account could not be created" do

    end
  end

end
