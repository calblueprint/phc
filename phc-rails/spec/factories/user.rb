require 'faker'

FactoryGirl.define do
  factory :user do |f|
    f.name { Faker::Name.first_name }
    f.email { Faker::Internet.email }
    f.auth_digest { User.new_token }
    f.password { "testpassword" }
    f.password_confirmation { "testpassword" }
  end
end