require 'rails_helper'

describe Account, type: :model do
  describe "#birthdate" do
    let(:account) { Account.new }

    it 'does not modify a properly formatted birthdate' do
      account.Birthdate__c = "1993-01-28"
      expect(account.birthdate).to eq("1993-01-28")
    end

    it 'returns N/A if birthdate is nil' do
      account.Birthdate__c = nil
      expect(account.birthdate).to eq('#N/A')
    end

    it 'pads values so length is consistent' do
      account.Birthdate__c = "93-2-1"
      expect(account.birthdate).to eq("1993-02-01")
    end

    it 'sets month and day to 01/01 if not specified' do
      account.Birthdate__c = "1993"
      expect(account.birthdate).to eq("1993-01-01")
    end
  end

  describe "#find_new_accounts" do
    it "returns accounts that need to be updated" do
      Account.delete_all
      account = Account.create(updated: false)
      expect(Account.find_new_accounts.length).to eq(1)
    end
  end

  describe "#full_name" do
    it 'formats the name' do
      account = Account.create(FirstName: "Foo", LastName: "Bar")
      expect(account.full_name).to eq("Foo Bar")
    end
  end

  describe "#to_hash" do
    it "returns a hash of the account" do
      account = Account.create(FirstName: "Foo", LastName: "Bar", sf_id: "xyz123")
      expect(account.to_hash).to eq(name: "Foo Bar", birthday: "#N/A", sf_id: "xyz123")
    end
  end
end
