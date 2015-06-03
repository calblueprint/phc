require 'rails_helper'

describe Account do
  it "has a valid factory" do
    FactoryGirl.create(:account).should be_valid
  end
  it "is invalid without a firstname"
  it "is invalid without a lastname"
  it "has a valid birthdate"
end
