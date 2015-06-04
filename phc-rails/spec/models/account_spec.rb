# == Schema Information
#
# Table name: accounts
#
#  id                                        :integer          not null, primary key
#  created_at                                :datetime
#  updated_at                                :datetime
#  sf_id                                     :string(255)
#  FirstName                                 :string(255)
#  LastName                                  :string(255)
#  SS_Num__c                                 :string(255)
#  Phone                                     :string(255)
#  PersonEmail                               :string(255)
#  Gender__c                                 :string(255)
#  Identify_as_GLBT__c                       :boolean
#  Race__c                                   :string(255)
#  Primary_Language__c                       :string(255)
#  Foster_Care__c                            :boolean
#  Veteran__c                                :boolean
#  Housing_Status_New__c                     :string(255)
#  How_long_have_you_been_homeless__c        :string(255)
#  Where_do_you_usually_go_for_healthcare__c :string(255)
#  Medical_Care_Other__c                     :string(255)
#  Birthdate__c                              :date
#

require 'rails_helper'

describe Account do
  it "has a valid factory" do
    FactoryGirl.create(:account).should be_valid
  end

  # # For now we allow nil first and last names

  # it "is invalid without a firstname" do
  #   FactoryGirl.build(:account, FirstName:nil).should_not be_valid
  # end

  # it "is invalid without a lastname" do
  #   FactoryGirl.build(:account, LastName:nil).should_not be_valid
  # end

  it "has a valid birthdate" do
    FactoryGirl.build(:account, Birthdate__c: "1/32/192").should_not be_valid
    FactoryGirl.build(:account, Birthdate__c: "1/32").should_not be_valid
    FactoryGirl.build(:account, Birthdate__c: nil).should be_valid
  end
end
