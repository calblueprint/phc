class Api::V1::EventRegistrationsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  # Creates an event registration and corresponding account
  # if it does not exist. Any services that were applied for
  # are set to applied for the given registration
  #
  # @param [String] account_sfid Salesforce ID of Account
  # @param [Integer] Number__c Unique ID for an event registration
  # @param [Boolean] *service True if given service was applied for
  def create
    sf_id =  params[:account_sfid]
    account = Account.find_by(sf_id: sf_id)
    account_params = params.permit(Account::API_FIELDS)
    account_params[:updated] = false

    # Update or create account with params
    if sf_id.blank? || sf_id == '""' || account.nil?
      account = Account.create(account_params)
    else
      account.update(account_params)
    end

    event_reg = account.event_registrations.create(Number__c: params[:Number__c])
    ServiceList.services.each do |name|
      event_reg.services.create(name: name).applied! if params[name].to_s == "true"
    end

    render json: { status: true, message: "Successfully saved event registration!" }
  end

  # Check if an event registration has been created for a given QR code
  # number.
  #
  # @param [Integer] Number__c Unique QR code for registration
  def search
    # Should probably change API to pass it as query param not header
    qr_code = request.headers["HTTP_NUMBER__C"]
    render json: { present: (EventRegistration.exists?(Number__c: qr_code) ? true : false) }
  end

  # Returns a list of the services applied for by a guest,
  # and their associated event registration
  #
  # @param [Integer] Number__c Unique QR code for registration
  def get_applied
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration
      applied_services = event_registration.services.where(status: Service.statuses[:applied]).map(&:name)
      render json: { status: "true", services: applied_services }
    else
      render status: 404, json: { message: "Event registration with that number does not exist." }
    end
  end

  # Updates the status for a service. This occurs when a
  # guest checks in at a service station.
  #
  # Unspecified => Drop In
  # Applied => Received
  # Drop In => Drop In (Client has already received service)
  # Received => Received (Client has already received service)
  #
  # @param [Integer] Number__c Unique QR code for registration
  # @param [String] service_name Name of the service
  def update_service
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration.nil?
      render status: 404, json: { message: "Event registration with that number does not exist." } and return
    end

    service = event_registration.services.find_by(name: params[:service_name])
    if service.nil?
      render status: 404, json: { message: "Service with that name does not exist." } and return
    end

    case service.status
    when "unspecified"
      service.drop_in!
      render json: { message: "Client's status set to drop-in." }
    when "applied"
      service.received!
      render json: { message: "Client's status set to received." }
    when "drop_in"
      render json: { message: "Client has already received service." }
    when "received"
      render json: { message: "Client has already received service." }
    else
      render status: 400, json: { message: "Invalid status." }
    end
  end

  def update_feedback
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration
      event_registration.update(event_feedback_params)
      render json: { message: "Successfully received feedback!" }
    else
      render status: 400, json: { message: "Event registration with that number does not exist." }
    end
  end

  private

  def event_feedback_params
    params.permit(:Number__c, :Experience__c, :Feedback__c, :Services_Needed__c => [])
  end

end
