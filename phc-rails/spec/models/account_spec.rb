# == Schema Information
#
# Table name: accounts
#
#  id                                        :integer          not null, primary key
#  created_at                                :datetime
#  updated_at                                :datetime
#  sf_id                                     :string
#  FirstName                                 :string
#  LastName                                  :string
#  SS_Num__c                                 :string
#  Birthdate__c                              :string
#  Phone                                     :string
#  PersonEmail                               :string
#  Gender__c                                 :string
#  Identify_as_GLBT__c                       :boolean
#  Race__c                                   :string
#  Primary_Language__c                       :string
#  Foster_Care__c                            :boolean
#  Veteran__c                                :boolean
#  Housing_Status_New__c                     :string
#  How_long_have_you_been_homeless__c        :string
#  Where_do_you_usually_go_for_healthcare__c :string
#  Medical_Care_Other__c                     :string
#  modified                                  :boolean          default(FALSE), not null
#

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
      account = Account.create(modified: true)
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
