require 'rails_helper'

describe User, type: :model do
  describe "#digest" do
    it "returns a hash digest of string" do
      expect(User.digest("test")).to_not be_nil
    end
  end

  describe "#new_token" do
    it "returns the auth token" do
      allow(ENV).to receive(:[]).and_return("secret_token")
      expect(User.new_token).to eq("secret_token")
    end
  end

  describe "#authenticated" do
    it "returns false if token is nil" do
      user = User.new(auth_token: nil)
      expect(user.authenticated? nil).to eq(false)
    end

    it "returns true if auth digest matches auth token" do
      allow(User).to receive(:new_token).and_return("token")
      user = User.new(email: "test@abc.com")
      user.remember
      expect(user.authenticated? "token").to eq(true)
    end
  end
end
