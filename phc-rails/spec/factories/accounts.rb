require 'faker'

FactoryGirl.define do
  factory :account do |f|
    f.FirstName { Faker::Name.first_name }
    f.LastName { Faker::Name.last_name }
  end
end
